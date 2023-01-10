package com.techteam.fabric.bettermod.util;

import com.techteam.fabric.bettermod.block.entity.BetterBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class InventoryUtil {
	public static Inventory getBlockInventory(ScreenHandlerContext ctx, int size) {
		return getBlockInventory(ctx, () -> new SimpleInventory(size));
	}
	private static Inventory getBlockInventory(ScreenHandlerContext ctx, Supplier<Inventory> fallback) {
		return ctx.get((world, pos) -> {
			BlockState state = world.getBlockState(pos);
			Block b = state.getBlock();
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof BetterBlockEntity blockEntity) {
				return blockEntity.getInventory();
			}

			return fallback.get();
		}).orElseGet(fallback);
	}
	public static @Nullable Inventory getInventoryAt(@NotNull World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof InventoryProvider inventoryProvider) {
			return inventoryProvider.getInventory(blockState, world, pos);
		} else if (blockState.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity && block instanceof ChestBlock chestBlock) {
				return ChestBlock.getInventory(chestBlock, blockState, world, pos, true);
			}
			if (blockEntity instanceof Inventory inventory) {
				return inventory;
			}
		}
		return null;
	}
	public static void readNbt(NbtCompound nbt, SimpleInventory stacks) {
		NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < nbtList.size(); ++i) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 0xFF;
			if (j >= stacks.size()) continue;
			stacks.setStack(j, ItemStack.fromNbt(nbtCompound));
		}
	}
	@Contract("_, _ -> param1")
	public static @NotNull NbtCompound writeNbt(NbtCompound nbt, SimpleInventory stacks) {
		NbtList nbtList = new NbtList();
		for (int i = 0; i < stacks.size(); ++i) {
			ItemStack itemStack = stacks.getStack(i);
			if (itemStack.isEmpty()) continue;
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putByte("Slot", (byte)i);
			itemStack.writeNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}
		nbt.put("Items", nbtList);
		return nbt;
	}

	public static @Nullable SingleSlotStorage<ItemVariant> getFirstTransferrableSlotCapSticky(@NotNull InventoryStorage from, InventoryStorage to, TransactionContext transactionContext) {
		try(Transaction transaction = Transaction.openNested(transactionContext)) {
			for(SingleSlotStorage<ItemVariant> slot : from.getSlots()) {
				if(!slot.isResourceBlank() && slot.getResource().getItem().getMaxCount() > 1 && canInsertExtractSticky(from, to, slot, transaction)) {
					return slot;
				}
			}
		}
		return null;
	}
	private static boolean canInsertExtractSticky(InventoryStorage from, InventoryStorage to, @NotNull SingleSlotStorage<ItemVariant> slot, TransactionContext context) {
		try(Transaction transaction = Transaction.openNested(context)) {
			return slot.simulateExtract(slot.getResource(),2,transaction) == 2 && to.simulateInsert(slot.getResource(), 1, transaction) == 1;
		}
	}

	public static boolean handleTransfer(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
		try (Transaction transaction = Transaction.openOuter()) {
			for (StorageView<ItemVariant> view : from) {
				if (view.isResourceBlank()) continue;
				ItemVariant resource = view.getResource();

				try (Transaction transferTransaction = transaction.openNested()) {
					// check how much can be inserted
					if(to.insert(resource, 1, transferTransaction) != 1) {
						continue;
					}
					if(view.extract(resource, 1, transferTransaction) == 1) {
						transferTransaction.commit();
						transaction.commit();
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean handleTransferSticky(@NotNull Storage<ItemVariant> from, Storage<ItemVariant> to) {
		try (Transaction transaction = Transaction.openOuter()) {
			for (StorageView<ItemVariant> view : from) {
				if (view.isResourceBlank()) {
					continue;
				}
				if(view.getAmount() <= 1) {
					continue;
				}
				ItemVariant resource = view.getResource();

				try (Transaction transferTransaction = transaction.openNested()) {
					// check how much can be inserted
					if(to.insert(resource, 1, transferTransaction) != 1) {
						continue;
					}
					if(view.extract(resource, 1, transferTransaction) == 1) {
						transferTransaction.commit();
						transaction.commit();
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean handleTransferStackable(@NotNull Storage<ItemVariant> from, Storage<ItemVariant> to) {
		try (Transaction transaction = Transaction.openOuter()) {
			for (StorageView<ItemVariant> view : from) {
				if (view.isResourceBlank()) {
					continue;
				}
				if(view.getCapacity() <= 1) {
					continue;
				}
				ItemVariant resource = view.getResource();

				try (Transaction transferTransaction = transaction.openNested()) {
					if(to.insert(resource, 1, transferTransaction) != 1) {
						continue;
					}
					if(view.extract(resource, 1, transferTransaction) == 1) {
						transferTransaction.commit();
						transaction.commit();
						return true;
					}
				}
			}
		}
		return false;
	}
}
