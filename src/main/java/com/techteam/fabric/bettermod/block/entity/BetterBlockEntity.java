package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.util.InventoryUtil;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class BetterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {
	public final SimpleInventory inventory;
	public final InventoryStorage SELF;
	protected final int size;
	// Unused for most.
	private final UUID uuid = UUID.randomUUID();

	public BetterBlockEntity(BlockEntityType<?> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState, int size) {
		super(blockEntityType, blockPos, blockState);
		this.size = size;
		this.inventory = new SimpleInventory(size) {
			@Override
			public boolean isValid(int slot, ItemStack stack) {
				return super.isValid(slot, stack) && BetterBlockEntity.this.isValid(slot, stack);
			}

			@Override
			public int getMaxCountPerStack() {
				int max = BetterBlockEntity.this.getMaxCountPerStack();
				if(max == -1) {
					return super.getMaxCountPerStack();
				} else {
					return max;
				}
			}

			@Override
			public void markDirty() {
				BetterBlockEntity.this.markDirty();
			}
		};
		this.SELF = InventoryStorage.of(this.inventory, null);
	}

	public void dropItems() {
		ItemScatterer.spawn(world, pos, inventory);
	}

	@Contract(pure = true)
	public final UUID getUUID() {
		return uuid;
	}

	@Override
	public void readNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		InventoryUtil.readNbt(tag, this.inventory, registryLookup);
		super.readNbt(tag, registryLookup);
	}

	@Override
	public void writeNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		InventoryUtil.writeNbt(tag, this.inventory, registryLookup);
		super.writeNbt(tag, registryLookup);
	}

	public boolean isValid(int slot, ItemStack stack) {
		return true;
	}
	public int getMaxCountPerStack() {
		return -1;
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
		return pos;
	}

	public Inventory getInventory() {
		return this.inventory;
	}
}
