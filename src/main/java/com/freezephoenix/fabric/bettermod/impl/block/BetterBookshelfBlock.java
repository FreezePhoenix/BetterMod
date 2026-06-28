package com.freezephoenix.fabric.bettermod.impl.block;

import com.mojang.serialization.MapCodec;
import com.freezephoenix.fabric.bettermod.api.block.BetterBlock;
import com.freezephoenix.fabric.bettermod.impl.block.entity.BetterBookshelfBlockEntity;
import org.jetbrains.annotations.NotNull;

public final class BetterBookshelfBlock extends BetterBlock<BetterBookshelfBlockEntity> {
	public static final MapCodec<BetterBookshelfBlock> CODEC = BetterBookshelfBlock.simpleCodec(BetterBookshelfBlock::new);

	public BetterBookshelfBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected @NotNull MapCodec<BetterBookshelfBlock> codec() {
		return CODEC;
	}
}
