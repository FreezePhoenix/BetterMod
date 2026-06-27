package com.freezephoenix.fabric.bettermod.impl.block.entity;

import com.freezephoenix.fabric.bettermod.impl.BetterMod;
import com.freezephoenix.fabric.bettermod.impl.util.Texts;
import com.freezephoenix.fabric.bettermod.impl.util.TransferType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BitHopperBlockEntity extends BetterHopperBlockEntity<BitHopperBlockEntity> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "bit_hopper");

	public BitHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.BIT_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
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
