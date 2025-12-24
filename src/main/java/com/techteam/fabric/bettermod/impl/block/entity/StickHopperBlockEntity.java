package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.util.Texts;
import com.techteam.fabric.bettermod.impl.util.TransferType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class StickHopperBlockEntity extends BetterExtractingHopperBlockEntity<StickHopperBlockEntity> {
	public static final Identifier ID = Identifier.of("bettermod", "stick_hopper");

	public StickHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.STICK_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public Text getContainerName() { return Texts.STICK_HOPPER; }

	@Override
	protected TransferType getExtractionTransferType() {
		return TransferType.STACKING;
	}

	@Override
	protected TransferType getInsertionTransferType() {
		return TransferType.STICKING;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return stack.isStackable() && super.isValid(slot, stack);
	}
}
