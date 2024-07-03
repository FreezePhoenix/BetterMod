package com.techteam.fabric.bettermod.impl.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.impl.block.entity.BitHopperBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class BitHopperBlock extends BetterHopperBlock<BitHopperBlockEntity> {
	public static final Identifier ID = Identifier.of("bettermod", "bithopper");
	public static final MapCodec<BitHopperBlock> CODEC = BitHopperBlock.createCodec(BitHopperBlock::new);

	@Override
	protected MapCodec<BitHopperBlock> getCodec() {
		return CODEC;
	}

	public BitHopperBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BitHopperBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BitHopperBlockEntity(pos, state);
	}
}
