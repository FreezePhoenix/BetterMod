package com.techteam.fabric.bettermod.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.api.block.BetterBlock;
import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RoomControllerBlock extends BetterBlock<RoomControllerBlockEntity> {
	public static final Identifier ID = Identifier.of("betterperf", "room_controller");
	public static final MapCodec<RoomControllerBlock> CODEC = RoomControllerBlock.createCodec(RoomControllerBlock::new);
	@Contract(pure = true)
	@Override
	protected MapCodec<RoomControllerBlock> getCodec() {
		return CODEC;
	}

	public RoomControllerBlock(@NotNull Settings settings) {
		super(settings.dynamicBounds().hardness(4.0f).nonOpaque());
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
		return (BlockState) renderView.getBlockEntityRenderData(pos);
	}

	@Contract("_, _ -> new")
	@Override
	public @NotNull RoomControllerBlockEntity createBlockEntity(@NotNull BlockPos pos, BlockState state) {
		return new RoomControllerBlockEntity(pos, state);
	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (world.getBlockEntity(pos) instanceof RoomControllerBlockEntity roomControllerBlockEntity) {
			if (roomControllerBlockEntity.disguised()) {
				return roomControllerBlockEntity.getVariantState().getCameraCollisionShape(world, pos, context);
			}
		}
		return super.getCameraCollisionShape(state, world, pos, context);
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, @NotNull BlockView world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof RoomControllerBlockEntity roomControllerBlockEntity) {
			if (roomControllerBlockEntity.disguised()) {
				return roomControllerBlockEntity.getVariantState().getAmbientOcclusionLightLevel(world, pos);
			}
		}
		return super.getAmbientOcclusionLightLevel(state, world, pos);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, @NotNull BlockView world, BlockPos pos, ShapeContext context) {
		if (world.getBlockEntity(pos) instanceof RoomControllerBlockEntity roomControllerBlockEntity)
			if (roomControllerBlockEntity.disguised()) {
				return roomControllerBlockEntity.getVariantState().getCollisionShape(world, pos);
			}
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, @NotNull BlockView world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof RoomControllerBlockEntity roomControllerBlockEntity) {
			if (roomControllerBlockEntity.disguised()) {
				return roomControllerBlockEntity.getVariantState().getCullingShape(world, pos);
			}
		}
		return super.getCullingShape(state, world, pos);
	}

	@Override
	public int getOpacity(BlockState state, @NotNull BlockView world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof RoomControllerBlockEntity roomControllerBlockEntity) {
			if (roomControllerBlockEntity.disguised()) {
				return roomControllerBlockEntity.getVariantState().getOpacity(world, pos);
			}
		}
		return super.getOpacity(state, world, pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, @NotNull BlockView world, BlockPos pos, ShapeContext context) {
		if (world.getBlockEntity(pos) instanceof RoomControllerBlockEntity roomControllerBlockEntity) {
			if (roomControllerBlockEntity.disguised()) {
				return roomControllerBlockEntity.getVariantState().getOutlineShape(world, pos);
			}
		}
		return super.getOutlineShape(state, world, pos, context);
	}

	@Override
	public boolean isShapeFullCube(BlockState state, @NotNull BlockView world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof RoomControllerBlockEntity roomControllerBlockEntity) {
			if (roomControllerBlockEntity.disguised()) {
				return roomControllerBlockEntity.getVariantState().isFullCube(world, pos);
			}
		}
		return super.isShapeFullCube(state, world, pos);
	}

	@Override
	public boolean isTransparent(BlockState state, @NotNull BlockView world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof RoomControllerBlockEntity roomControllerBlockEntity) {
			if (roomControllerBlockEntity.disguised()) {
				return roomControllerBlockEntity.getVariantState().isTransparent(world, pos);
			}
		}
		return super.isTransparent(state, world, pos);
	}
}
