package com.techteam.fabric.bettermod.impl.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.impl.block.entity.BitHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public final class BitHopperBlock extends BetterHopperBlock<BitHopperBlockEntity> {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "bithopper");
	public static final MapCodec<BitHopperBlock> CODEC = BitHopperBlock.simpleCodec(BitHopperBlock::new);
	private static final VoxelShape TOP_SHAPE = Shapes.or(Block.box(
			0.0,
			10.0,
			0.0,
			16.0,
			12.0,
			16.0
	), Block.box(1.0, 12.0, 1.0, 15.0, 13.0, 15.0));
	private static final VoxelShape MIDDLE_SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
	private static final VoxelShape OUTSIDE_SHAPE = Shapes.or(MIDDLE_SHAPE, TOP_SHAPE);
	private static final VoxelShape DEFAULT_SHAPE = OUTSIDE_SHAPE;
	private static final VoxelShape DOWN_SHAPE = Shapes.or(
			DEFAULT_SHAPE,
			Block.box(
					6.0,
					0.0,
					6.0,
					10.0,
					4.0,
					10.0
			)
	);
	private static final VoxelShape EAST_SHAPE = Shapes.or(
			DEFAULT_SHAPE,
			Block.box(
					12.0,
					4.0,
					6.0,
					16.0,
					8.0,
					10.0
			)
	);
	private static final VoxelShape NORTH_SHAPE = Shapes.or(
			DEFAULT_SHAPE,
			Block.box(
					6.0,
					4.0,
					0.0,
					10.0,
					8.0,
					4.0
			)
	);
	private static final VoxelShape SOUTH_SHAPE = Shapes.or(
			DEFAULT_SHAPE,
			Block.box(
					6.0,
					4.0,
					12.0,
					10.0,
					8.0,
					16.0
			)
	);
	private static final VoxelShape WEST_SHAPE = Shapes.or(
			DEFAULT_SHAPE,
			Block.box(
					0.0,
					4.0,
					6.0,
					4.0,
					8.0,
					10.0
			)
	);
	private static final VoxelShape DOWN_RAYCAST_SHAPE = Shapes.empty();
	private static final VoxelShape EAST_RAYCAST_SHAPE = Block.box(12.0, 8.0, 6.0, 16.0, 10.0, 10.0);
	private static final VoxelShape NORTH_RAYCAST_SHAPE = Block.box(6.0, 8.0, 0.0, 10.0, 10.0, 4.0);
	private static final VoxelShape SOUTH_RAYCAST_SHAPE = Block.box(6.0, 8.0, 12.0, 10.0, 10.0, 16.0);
	private static final VoxelShape WEST_RAYCAST_SHAPE = Block.box(0.0, 8.0, 6.0, 4.0, 10.0, 10.0);

	@Override
	protected MapCodec<BitHopperBlock> codec() {
		return CODEC;
	}

	public BitHopperBlock(Properties settings) {
		super(settings);
	}

	@Override
	public VoxelShape getShape(@NotNull BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(HopperBlock.FACING)) {
			case NORTH -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			case EAST -> EAST_SHAPE;
			default -> DOWN_SHAPE;
		};
	}

	@Override
	public VoxelShape getInteractionShape(@NotNull BlockState state, BlockGetter world, BlockPos pos) {
		return switch (state.getValue(HopperBlock.FACING)) {
			case NORTH -> NORTH_RAYCAST_SHAPE;
			case SOUTH -> SOUTH_RAYCAST_SHAPE;
			case WEST -> WEST_RAYCAST_SHAPE;
			case EAST -> EAST_RAYCAST_SHAPE;
			default -> DOWN_RAYCAST_SHAPE;
		};
	}
}
