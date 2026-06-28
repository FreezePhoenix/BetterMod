package com.freezephoenix.fabric.bettermod.impl.block.entity;

import com.freezephoenix.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.freezephoenix.fabric.bettermod.impl.BetterMod;
import com.freezephoenix.fabric.bettermod.impl.client.gui.BetterBookshelfScreenHandler;
import com.freezephoenix.fabric.bettermod.impl.util.ItemTagKeys;
import com.freezephoenix.fabric.bettermod.impl.util.Texts;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BetterBookshelfBlockEntity extends BetterBlockEntity<BetterBookshelfBlockEntity> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("minecraft", "bookshelf");

	public BetterBookshelfBlockEntity(BlockPos pos, BlockState state) {
		super(BetterMod.BOOKSHELF_BLOCK_ENTITY_TYPE, pos, state, 16);
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
