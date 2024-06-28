package com.techteam.fabric.bettermod.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.block.entity.StickHopperBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class StickHopperBlock extends BetterHopperBlock<StickHopperBlockEntity> {
	public static final Identifier ID = Identifier.of("bettermod", "stickhopper");
	public static final MapCodec<StickHopperBlock> CODEC = StickHopperBlock.createCodec(StickHopperBlock::new);
	@Contract(pure = true)
	@Override
	protected MapCodec<StickHopperBlock> getCodec() {
		return CODEC;
	}
	private static final VoxelShape TOP_SHAPE;
	private static final VoxelShape MIDDLE_SHAPE;
	private static final VoxelShape OUTSIDE_SHAPE;
	private static final VoxelShape INSIDE_SHAPE;
	private static final VoxelShape DEFAULT_SHAPE;
	private static final VoxelShape DOWN_SHAPE;
	private static final VoxelShape EAST_SHAPE;
	private static final VoxelShape NORTH_SHAPE;
	private static final VoxelShape SOUTH_SHAPE;
	private static final VoxelShape WEST_SHAPE;
	private static final VoxelShape DOWN_RAYCAST_SHAPE;
	private static final VoxelShape EAST_RAYCAST_SHAPE;
	private static final VoxelShape NORTH_RAYCAST_SHAPE;
	private static final VoxelShape SOUTH_RAYCAST_SHAPE;
	private static final VoxelShape WEST_RAYCAST_SHAPE;

	static {
		TOP_SHAPE = Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
		MIDDLE_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
		OUTSIDE_SHAPE = VoxelShapes.union(MIDDLE_SHAPE, TOP_SHAPE);
		INSIDE_SHAPE = createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
		DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(OUTSIDE_SHAPE, INSIDE_SHAPE, BooleanBiFunction.ONLY_FIRST);
		DOWN_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
		EAST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
		NORTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
		SOUTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
		WEST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
		DOWN_RAYCAST_SHAPE = INSIDE_SHAPE;
		EAST_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
		NORTH_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
		SOUTH_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
		WEST_RAYCAST_SHAPE = VoxelShapes.union(INSIDE_SHAPE, Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
	}
	public StickHopperBlock(@NotNull Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(@NotNull BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
	public VoxelShape getRaycastShape(@NotNull BlockState state, BlockView world, BlockPos pos) {
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
				return INSIDE_SHAPE;
			}
		}
	}

	@Contract("_, _ -> new")
	@Override
	public @NotNull StickHopperBlockEntity createBlockEntity(@NotNull BlockPos pos, BlockState state) {
		return new StickHopperBlockEntity(pos, state);
	}
}
