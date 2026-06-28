package com.freezephoenix.fabric.bettermod.impl.block;

import com.mojang.serialization.MapCodec;
import com.freezephoenix.fabric.bettermod.impl.block.entity.PullHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public final class PullHopperBlock extends BetterHopperBlock<PullHopperBlockEntity> {
	private static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "pullhopper");
	public static final BlockItemId BlockItemID = BlockItemId.create(ID, ID);
	public static final MapCodec<PullHopperBlock> CODEC = PullHopperBlock.simpleCodec(PullHopperBlock::new);
	private static final VoxelShape TOP_SHAPE = Shapes.or(Block.box(
			2.0,
			10.0,
			0.0,
			14.0,
			16.0,
			16.0
	), Block.box(0.0, 10.0, 2.0, 16.0, 16.0, 14.0));
	private static final VoxelShape MIDDLE_SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
	private static final VoxelShape OUTSIDE_SHAPE = Shapes.or(MIDDLE_SHAPE, TOP_SHAPE);
	private static final VoxelShape INSIDE_SHAPE = Shapes.or(Block.box(
			4.0,
			11.0,
			2.0,
			12.0,
			16.0,
			14.0
	), Block.box(2.0, 11.0, 4.0, 14.0, 16.0, 12.0));
	private static final VoxelShape DEFAULT_SHAPE = Shapes.join(
			OUTSIDE_SHAPE,
			INSIDE_SHAPE,
			BooleanOp.ONLY_FIRST
	);
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
	private static final VoxelShape DOWN_RAYCAST_SHAPE = INSIDE_SHAPE;
	private static final VoxelShape EAST_RAYCAST_SHAPE = Shapes.or(
			INSIDE_SHAPE,
			Block.box(
					12.0,
					8.0,
					6.0,
					16.0,
					10.0,
					10.0
			)
	);
	private static final VoxelShape NORTH_RAYCAST_SHAPE = Shapes.or(
			INSIDE_SHAPE,
			Block.box(
					6.0,
					8.0,
					0.0,
					10.0,
					10.0,
					4.0
			)
	);
	private static final VoxelShape SOUTH_RAYCAST_SHAPE = Shapes.or(
			INSIDE_SHAPE,
			Block.box(
					6.0,
					8.0,
					12.0,
					10.0,
					10.0,
					16.0
			)
	);
	private static final VoxelShape WEST_RAYCAST_SHAPE = Shapes.or(
			INSIDE_SHAPE,
			Block.box(
					0.0,
					8.0,
					6.0,
					4.0,
					10.0,
					10.0
			)
	);

	@Override
	protected @NotNull MapCodec<PullHopperBlock> codec() {
		return CODEC;
	}

	public PullHopperBlock(Properties settings) {
		super(settings);
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return switch (state.getValue(HopperBlock.FACING)) {
			case NORTH -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			case EAST -> EAST_SHAPE;
			default -> DOWN_SHAPE;
		};
	}

	@Override
	public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
		return switch (state.getValue(HopperBlock.FACING)) {
			case NORTH -> NORTH_RAYCAST_SHAPE;
			case SOUTH -> SOUTH_RAYCAST_SHAPE;
			case WEST -> WEST_RAYCAST_SHAPE;
			case EAST -> EAST_RAYCAST_SHAPE;
			default -> DOWN_RAYCAST_SHAPE;
		};
	}
}
