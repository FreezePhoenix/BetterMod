package com.techteam.fabric.bettermod.util;

import com.techteam.fabric.bettermod.block.entity.BetterBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class InventoryUtil {
	public static Inventory getBlockInventory(ScreenHandlerContext ctx, int size) {
		return getBlockInventory(ctx, () -> new SimpleInventory(size));
	}

	private static Inventory getBlockInventory(ScreenHandlerContext ctx, Supplier<Inventory> fallback) {
		return ctx.get((world, pos) -> {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof BetterBlockEntity blockEntity) {
				return blockEntity.getInventory();
			}

			return null;
		}).orElseGet(fallback);
	}

	public static void readNbt(@NotNull NbtCompound nbt, @NotNull SimpleInventory stacks) {
		NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < nbtList.size(); ++i) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 0xFF;
			if (j >= stacks.size()) continue;
			stacks.setStack(j, ItemStack.fromNbt(nbtCompound));
		}
	}

	public static void writeNbt(@NotNull NbtCompound nbt, @NotNull SimpleInventory stacks) {
		NbtList nbtList = new NbtList();
		for (int i = 0; i < stacks.size(); ++i) {
			ItemStack itemStack = stacks.getStack(i);
			if (itemStack.isEmpty()) continue;
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putByte("Slot", (byte) i);
			itemStack.writeNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}
		nbt.put("Items", nbtList);
	}

	public static boolean handleTransfer(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
		for (StorageView<ItemVariant> view : from) {
			if (view.isResourceBlank()) continue;
			ItemVariant resource = view.getResource();

			try (Transaction transferTransaction = Transaction.openOuter()) {
				// check how much can be inserted
				if (to.insert(resource, 1, transferTransaction) == 1 && view.extract(resource, 1, transferTransaction) == 1) {
					transferTransaction.commit();
					return true;
				}
			}
		}
		return false;
	}

	public static boolean handleTransferSticky(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
		for (StorageView<ItemVariant> view : from) {
			if (view.isResourceBlank()) {
				continue;
			}
			if (view.getAmount() <= 1) {
				continue;
			}
			ItemVariant resource = view.getResource();

			try (Transaction transferTransaction = Transaction.openOuter()) {
				// check how much can be inserted
				if (to.insert(resource, 1, transferTransaction) == 1 && view.extract(resource, 1, transferTransaction) == 1) {
					transferTransaction.commit();
					return true;
				}
			}
		}
		return false;
	}

	public static boolean handleTransferStackable(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
		for (StorageView<ItemVariant> view : from) {
			if (view.isResourceBlank()) {
				continue;
			}
			if (view.getCapacity() <= 1) {
				continue;
			}
			ItemVariant resource = view.getResource();

			try (Transaction transferTransaction = Transaction.openOuter()) {
				if (to.insert(resource, 1, transferTransaction) == 1 && view.extract(resource, 1, transferTransaction) == 1) {
					transferTransaction.commit();
					return true;
				}
			}
		}
		return false;
	}
}
