package com.techteam.fabric.bettermod.impl.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.impl.block.entity.BitHopperBlockEntity;
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
import org.jetbrains.annotations.NotNull;

public final class BitHopperBlock extends BetterHopperBlock<BitHopperBlockEntity> {
	public static final Identifier ID = Identifier.of("bettermod", "bithopper");
	public static final MapCodec<BitHopperBlock> CODEC = BitHopperBlock.createCodec(BitHopperBlock::new);
	private static final VoxelShape TOP_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(1.0, 12.0, 1.0, 15.0, 13.0, 15.0));
	private static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
	private static final VoxelShape OUTSIDE_SHAPE = VoxelShapes.union(MIDDLE_SHAPE, TOP_SHAPE);
	private static final VoxelShape DEFAULT_SHAPE = OUTSIDE_SHAPE;
	private static final VoxelShape DOWN_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
	private static final VoxelShape EAST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
	private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
	private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
	private static final VoxelShape WEST_SHAPE = VoxelShapes.union(DEFAULT_SHAPE, Block.createCuboidShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
	private static final VoxelShape DOWN_RAYCAST_SHAPE = VoxelShapes.empty();
	private static final VoxelShape EAST_RAYCAST_SHAPE = Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0);
	private static final VoxelShape NORTH_RAYCAST_SHAPE = Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0);
	private static final VoxelShape SOUTH_RAYCAST_SHAPE = Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0);
	private static final VoxelShape WEST_RAYCAST_SHAPE = Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0);

	@Override
	protected MapCodec<BitHopperBlock> getCodec() {
		return CODEC;
	}

	public BitHopperBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(@NotNull BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return switch (state.get(HopperBlock.FACING)) {
			case NORTH -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			case EAST -> EAST_SHAPE;
			default -> DOWN_SHAPE;
		};
	}

	@Override
	public VoxelShape getRaycastShape(@NotNull BlockState state, BlockView world, BlockPos pos) {
		return switch (state.get(HopperBlock.FACING)) {
			case NORTH -> NORTH_RAYCAST_SHAPE;
			case SOUTH -> SOUTH_RAYCAST_SHAPE;
			case WEST -> WEST_RAYCAST_SHAPE;
			case EAST -> EAST_RAYCAST_SHAPE;
			default -> DOWN_RAYCAST_SHAPE;
		};
	}


	@Override
	public BitHopperBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BitHopperBlockEntity(pos, state);
	}
}
