package com.techteam.fabric.bettermod.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.block.entity.BitHopperBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BitHopperBlock extends BetterHopperBlock<BitHopperBlockEntity> {
	public static final Identifier ID = Identifier.of("bettermod", "bithopper");
	public static final MapCodec<BitHopperBlock> CODEC = BitHopperBlock.createCodec(BitHopperBlock::new);
	@Contract(pure = true)
	@Override
	protected MapCodec<BitHopperBlock> getCodec() {
		return CODEC;
	}

	public BitHopperBlock(@NotNull Settings settings) {
		super(settings);
	}

	@Contract("_, _ -> new")
	@Override
	public @NotNull BitHopperBlockEntity createBlockEntity(@NotNull BlockPos pos, BlockState state) {
		return new BitHopperBlockEntity(pos, state);
	}
}
