package com.techteam.fabric.bettermod.impl.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.api.block.BetterBlock;
import com.techteam.fabric.bettermod.impl.block.entity.BetterBookshelfBlockEntity;

public final class BetterBookshelfBlock extends BetterBlock<BetterBookshelfBlockEntity> {
	public static final MapCodec<BetterBookshelfBlock> CODEC = BetterBookshelfBlock.simpleCodec(BetterBookshelfBlock::new);

	public BetterBookshelfBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected MapCodec<BetterBookshelfBlock> codec() {
		return CODEC;
	}
}
