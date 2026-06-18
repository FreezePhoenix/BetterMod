package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.util.Texts;
import com.techteam.fabric.bettermod.impl.util.TransferType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PullHopperBlockEntity extends BetterExtractingHopperBlockEntity<PullHopperBlockEntity> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "pull_hopper");

	public PullHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.PULL_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public Component getDefaultName() {
		return Texts.PULL_HOPPER;
	}

	@Override
	protected TransferType getExtractionTransferType() {
		return TransferType.STANDARD;
	}

	@Override
	protected TransferType getInsertionTransferType() {
		return TransferType.STANDARD;
	}
}
