package com.techteam.fabric.bettermod.impl.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.impl.block.entity.PullHopperBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class PullHopperBlock extends BetterHopperBlock<PullHopperBlockEntity> {
	public static final Identifier ID = Identifier.of("bettermod", "pullhopper");
	public static final MapCodec<PullHopperBlock> CODEC = PullHopperBlock.createCodec(PullHopperBlock::new);

	@Override
	protected MapCodec<PullHopperBlock> getCodec() {
		return CODEC;
	}

	public PullHopperBlock(Settings settings) {
		super(settings);
	}

	@Override
	public PullHopperBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PullHopperBlockEntity(pos, state);
	}
}
