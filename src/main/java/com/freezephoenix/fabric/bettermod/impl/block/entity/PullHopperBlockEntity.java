package com.freezephoenix.fabric.bettermod.impl.block.entity;

import com.freezephoenix.fabric.bettermod.impl.BetterMod;
import com.freezephoenix.fabric.bettermod.impl.util.Texts;
import com.freezephoenix.fabric.bettermod.impl.util.TransferType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public class PullHopperBlockEntity extends BetterExtractingHopperBlockEntity<PullHopperBlockEntity> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "pull_hopper");

	public PullHopperBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BetterMod.PULL_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	protected Component getDefaultName() {
		return Texts.PULL_HOPPER;
	}

	@Override
	protected TransferType getTransferType() {
		return TransferType.STANDARD;
	}
}
