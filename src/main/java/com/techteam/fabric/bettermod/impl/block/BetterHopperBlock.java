package com.techteam.fabric.bettermod.impl.block;

import com.techteam.fabric.bettermod.api.block.BetterTickingBlock;
import com.techteam.fabric.bettermod.impl.block.entity.BetterHopperBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public abstract class BetterHopperBlock<T extends BetterHopperBlockEntity<T>> extends BetterTickingBlock<T> {
	public BetterHopperBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide().getOpposite();
		return this.getDefaultState().with(
				HopperBlock.FACING,
				direction.getAxis() == Direction.Axis.Y
				? Direction.DOWN
				: direction
		);
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HopperBlock.FACING);
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return StorageUtil.calculateComparatorOutput(ItemStorage.SIDED.find(world, pos, null));
	}

	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		// Present for Lithium compat.
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		// Present for Lithium compat.
		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
	}
}
