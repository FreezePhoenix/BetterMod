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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class BetterBlockEntity extends LootableContainerBlockEntity implements SidedStorageBlockEntity {
	protected DefaultedList<ItemStack> inventory;
	public SlottedStorage<ItemVariant> SELF;
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
	public void readNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		if (tag.containsUuid(Entity.UUID_KEY)) {
			uuid = tag.getUuid(Entity.UUID_KEY);
		}
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.readLootTable(tag)) {
			Inventories.readNbt(tag, this.getHeldStacks(), registryLookup);
		}
		super.readNbt(tag, registryLookup);
	}

	@Override
	public void writeNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putUuid(Entity.UUID_KEY, this.getUUID());
		if (!this.writeLootTable(tag)) {
			Inventories.writeNbt(tag, this.getHeldStacks(), registryLookup);
		}
		super.writeNbt(tag, registryLookup);
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
