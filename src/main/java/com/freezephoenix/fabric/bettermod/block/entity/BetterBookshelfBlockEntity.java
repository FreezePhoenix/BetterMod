package com.freezephoenix.fabric.bettermod.block.entity;

import com.freezephoenix.fabric.api.block.entity.InventoryBetterBlockEntity;
import com.freezephoenix.fabric.bettermod.BetterMod;
import com.freezephoenix.fabric.bettermod.gui.BetterBookshelfScreenHandler;
import com.freezephoenix.fabric.bettermod.util.ItemTagKeys;
import com.freezephoenix.fabric.bettermod.util.Texts;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BetterBookshelfBlockEntity extends InventoryBetterBlockEntity<BetterBookshelfBlockEntity> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("minecraft", "bookshelf");

	public BetterBookshelfBlockEntity(BlockPos pos, BlockState state) {
		super(BetterMod.BOOKSHELF.entity(), pos, state, 16);
	}

	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new BetterBookshelfScreenHandler(syncId, playerInventory, this);
	}

	@Override
	protected Component getDefaultName() {
		return Texts.BOOKSHELF;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return stack.is(ItemTagKeys.SHELVABLE);
	}
}
