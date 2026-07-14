package com.freezephoenix.fabric.bettermod.block.entity;

import com.freezephoenix.fabric.bettermod.BetterMod;
import com.freezephoenix.fabric.bettermod.util.Texts;
import com.freezephoenix.fabric.bettermod.util.TransferType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public class BitHopperBlockEntity extends BetterHopperBlockEntity<BitHopperBlockEntity> {

	public static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "bit_hopper");

	public BitHopperBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BetterMod.BIT_HOPPER.entity(), blockPos, blockState);
	}

	@Override
	protected Component getDefaultName() {
		return Texts.BIT_HOPPER;
	}

	@Override
	protected TransferType getTransferType() {
		return TransferType.STANDARD;
	}
}
