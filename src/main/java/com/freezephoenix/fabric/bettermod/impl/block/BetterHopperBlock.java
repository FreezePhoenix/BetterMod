package com.freezephoenix.fabric.bettermod.impl.block;

import com.freezephoenix.fabric.bettermod.api.block.BetterTickingBlock;
import com.freezephoenix.fabric.bettermod.impl.block.entity.BetterHopperBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public abstract class BetterHopperBlock<T extends BetterHopperBlockEntity<T>> extends BetterTickingBlock<T> {
	public BetterHopperBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
		return false;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Direction direction = ctx.getClickedFace().getOpposite();
		return this.defaultBlockState().setValue(
				HopperBlock.FACING,
				direction.getAxis() == Direction.Axis.Y
				? Direction.DOWN
				: direction
		);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HopperBlock.FACING);
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
		return StorageUtil.getRedstoneSignal(ItemStorage.SIDED.find(world, pos, direction));
	}
}
