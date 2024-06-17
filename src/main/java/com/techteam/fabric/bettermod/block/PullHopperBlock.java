package com.techteam.fabric.bettermod.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.block.entity.PullHopperBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PullHopperBlock extends BetterTickingBlock<PullHopperBlockEntity> {
	public static final Identifier ID = Identifier.of("bettermod", "pullhopper");
	public static final MapCodec<PullHopperBlock> CODEC = PullHopperBlock.createCodec(PullHopperBlock::new);
	@Contract(pure = true)
	@Override
	protected MapCodec<PullHopperBlock> getCodec() {
		return CODEC;
	}

	public PullHopperBlock(@NotNull Settings settings) {
		super(settings);
	}

	protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
		builder.add(HopperBlock.FACING);
	}

	@Contract("_, _ -> new")
	@Override
	public @NotNull PullHopperBlockEntity createBlockEntity(@NotNull BlockPos pos, BlockState state) {
		return new PullHopperBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
		Direction direction = ctx.getSide().getOpposite();
		return this.getDefaultState().with(
				HopperBlock.FACING,
				direction.getAxis() == Direction.Axis.Y
				? Direction.DOWN
				: direction
		);
	}


}
