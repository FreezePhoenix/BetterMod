package com.techteam.fabric.bettermod.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class BetterBlockEntity extends LootableContainerBlockEntity implements ExtendedScreenHandlerFactory {
	protected DefaultedList<ItemStack> inventory;
	protected int size;
	private UUID uuid = UUID.randomUUID();

	public BetterBlockEntity(BlockEntityType<?> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState) {
		this(blockEntityType, blockPos, blockState, 0);
	}

	public BetterBlockEntity(BlockEntityType<?> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState, int size) {
		super(blockEntityType, blockPos, blockState);
		this.size = size;
		this.inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	public void dropItems() {
		ItemScatterer.spawn(world, pos, inventory);
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> new_inv) {
		this.inventory = new_inv;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public void readNbt(@NotNull NbtCompound tag) {
		super.readNbt(tag);
		if (tag.containsUuid("uuid")) {
			setUUID(tag.getUuid("uuid"));
		} else if (tag.contains("uuid", NbtElement.STRING_TYPE)) {
			setUUID(UUID.fromString(tag.getString("uuid")));
		}
		if (!this.deserializeLootTable(tag)) {
			Inventories.readNbt(tag, this.inventory);
		}
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public void writeNbt(@NotNull NbtCompound tag) {
		super.writeNbt(tag);
		tag.putUuid("uuid", this.getUUID());
		if (!this.serializeLootTable(tag)) {
			Inventories.writeNbt(tag, this.inventory);
		}
	}

}
