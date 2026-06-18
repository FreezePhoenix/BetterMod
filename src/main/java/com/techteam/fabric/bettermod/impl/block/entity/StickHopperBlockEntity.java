package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.util.Texts;
import com.techteam.fabric.bettermod.impl.util.TransferType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class StickHopperBlockEntity extends BetterExtractingHopperBlockEntity<StickHopperBlockEntity> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "stick_hopper");

	public StickHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.STICK_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public Component getDefaultName() { return Texts.STICK_HOPPER; }

	@Override
	protected TransferType getExtractionTransferType() {
		return TransferType.STACKING;
	}

	@Override
	protected TransferType getInsertionTransferType() {
		return TransferType.STICKING;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return stack.isStackable() && super.canPlaceItem(slot, stack);
	}
}
