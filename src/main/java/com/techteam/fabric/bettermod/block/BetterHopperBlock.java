package com.techteam.fabric.bettermod.block;

import com.techteam.fabric.bettermod.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.block.entity.ITickable;
import com.techteam.fabric.bettermod.block.entity.PullHopperBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BetterHopperBlock<T extends BetterBlockEntity & ITickable> extends BetterTickingBlock<T> {
	public BetterHopperBlock(@NotNull Settings settings) {
		super(settings);
	}

	@Override
	protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
		return false;
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

	protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
		builder.add(HopperBlock.FACING);
	}
}
