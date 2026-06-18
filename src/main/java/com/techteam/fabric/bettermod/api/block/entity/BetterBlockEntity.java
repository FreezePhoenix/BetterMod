package com.techteam.fabric.bettermod.api.block.entity;

import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.client.MouseHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class BetterBlockEntity<T extends BetterBlockEntity<T>> extends RandomizableContainerBlockEntity implements SidedStorageBlockEntity {
	protected NonNullList<ItemStack> inventory;
	public final SlottedStorage<ItemVariant> SELF;
	private final int size;

	public BetterBlockEntity(BlockEntityType<T> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState, int size) {
		super(blockEntityType, blockPos, blockState);
		this.size = size;
		this.inventory = NonNullList.withSize(size, ItemStack.EMPTY);
		this.SELF = ContainerStorage.of(this, null);
	}

	@Override
	protected void loadAdditional(ValueInput view) {
		super.loadAdditional(view);
		if (!this.tryLoadLootTable(view)) {
			ContainerHelper.loadAllItems(view, inventory);
		}
	}

	@Override
	public void saveAdditional(ValueOutput view) {
		super.saveAdditional(view);
		if (!this.trySaveLootTable(view)) {
			ContainerHelper.saveAllItems(view, this.inventory);
		}
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.inventory;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> inventory) {
		this.inventory = inventory;
	}

	@Override
	public int getContainerSize() {
		return size;
	}

	@Override
	public @Nullable Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
		return SELF;
	}
}
