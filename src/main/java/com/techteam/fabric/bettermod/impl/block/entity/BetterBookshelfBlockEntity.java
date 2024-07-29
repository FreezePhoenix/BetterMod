package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.client.gui.BetterBookshelfScreenHandler;
import com.techteam.fabric.bettermod.impl.util.ItemTagKeys;
import com.techteam.fabric.bettermod.impl.util.Texts;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class BetterBookshelfBlockEntity extends BetterBlockEntity {
	public static final Identifier ID = Identifier.of("minecraft", "bookshelf");

	public BetterBookshelfBlockEntity(BlockPos pos, BlockState state) {
		super(BetterMod.BOOKSHELF_BLOCK_ENTITY_TYPE, pos, state, 16);
	}

	@Override
	public @NotNull ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new BetterBookshelfScreenHandler(syncId, playerInventory, this);
	}

	@Override
	public Text getContainerName() {
		return Texts.BOOKSHELF;
	}

	@Override
	public boolean isValid(int slot, @NotNull ItemStack stack) {
		return stack.isIn(ItemTagKeys.SHELVABLE) && super.isValid(slot, stack);
	}
}
