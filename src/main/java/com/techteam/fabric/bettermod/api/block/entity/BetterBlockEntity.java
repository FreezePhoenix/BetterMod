package com.techteam.fabric.bettermod.api.block.entity;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class BetterBlockEntity extends LootableContainerBlockEntity implements SidedStorageBlockEntity {
	protected DefaultedList<ItemStack> inventory;
	public final SlottedStorage<ItemVariant> SELF;
	protected final int size;

	// Unused for most.
	private UUID uuid = UUID.randomUUID();

	public BetterBlockEntity(BlockEntityType<?> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState, int size) {
		super(blockEntityType, blockPos, blockState);
		this.size = size;
		this.inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
		this.SELF = InventoryStorage.of(BetterBlockEntity.this, null);
	}

	@Contract(pure = true)
	public final UUID getUUID() {
		return uuid;
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);
		view.read(Entity.UUID_KEY, Uuids.INT_STREAM_CODEC).ifPresent(uuid -> this.uuid = uuid);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.readLootTable(view)) {
			Inventories.readData(view, this.inventory);
		}
	}

	@Override
	public void writeData(WriteView view) {
		super.writeData(view);
		view.put(Entity.UUID_KEY, Uuids.INT_STREAM_CODEC, this.getUUID());
		if (!this.writeLootTable(view)) {
			Inventories.writeData(view, this.inventory);
		}
	}

	@Override
	protected DefaultedList<ItemStack> getHeldStacks() {
		return this.inventory;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
		this.inventory = inventory;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
		return SELF;
	}
}
