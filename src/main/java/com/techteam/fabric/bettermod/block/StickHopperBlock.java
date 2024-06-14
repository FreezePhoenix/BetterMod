package com.techteam.fabric.bettermod.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.block.entity.PullHopperBlockEntity;
import com.techteam.fabric.bettermod.block.entity.StickHopperBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.Hopper;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StickHopperBlock extends BetterTickingBlock<StickHopperBlockEntity> {
	public static final Identifier ID = new Identifier("bettermod", "stickhopper");
	public static final MapCodec<StickHopperBlock> CODEC = StickHopperBlock.createCodec(StickHopperBlock::new);
	@Override
	protected MapCodec<StickHopperBlock> getCodec() {
		return CODEC;
	}

	private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
	private static final VoxelShape OUTSIDE_SHAPE = VoxelShapes.union(MIDDLE_SHAPE, TOP_SHAPE);
	private static final VoxelShape DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(OUTSIDE_SHAPE, Hopper.INSIDE_SHAPE, BooleanBiFunction.ONLY_FIRST);
	private static final VoxelShape DOWN_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
	private static final VoxelShape EAST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
	private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
	private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
	private static final VoxelShape WEST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
	private static final VoxelShape DOWN_RAYCAST_SHAPE = Hopper.INSIDE_SHAPE;
	private static final VoxelShape EAST_RAYCAST_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
	private static final VoxelShape NORTH_RAYCAST_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
	private static final VoxelShape SOUTH_RAYCAST_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
	private static final VoxelShape WEST_RAYCAST_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));

	public StickHopperBlock(@NotNull Settings settings) {
		super(settings);
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HopperBlock.FACING);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch (state.get(HopperBlock.FACING)) {
			case DOWN -> {
				return DOWN_SHAPE;
			}
			case NORTH -> {
				return NORTH_SHAPE;
			}
			case SOUTH -> {
				return SOUTH_SHAPE;
			}
			case WEST -> {
				return WEST_SHAPE;
			}
			case EAST -> {
				return EAST_SHAPE;
			}
		}
		return DEFAULT_SHAPE;
	}

	@Override
	public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
		switch (state.get(HopperBlock.FACING)) {
			case DOWN -> {
				return DOWN_RAYCAST_SHAPE;
			}
			case NORTH -> {
				return NORTH_RAYCAST_SHAPE;
			}
			case SOUTH -> {
				return SOUTH_RAYCAST_SHAPE;
			}
			case WEST -> {
				return WEST_RAYCAST_SHAPE;
			}
			case EAST -> {
				return EAST_RAYCAST_SHAPE;
			}
			default -> {
				return Hopper.INSIDE_SHAPE;
			}
		}
	}

	@Contract("_, _ -> new")
	@Override
	public @NotNull StickHopperBlockEntity createBlockEntity(@NotNull BlockPos pos, BlockState state) {
		return new StickHopperBlockEntity(pos, state);
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


}
