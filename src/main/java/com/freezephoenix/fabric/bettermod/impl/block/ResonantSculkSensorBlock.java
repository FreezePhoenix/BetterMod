package com.freezephoenix.fabric.bettermod.impl.block;

import com.freezephoenix.fabric.bettermod.api.block.BetterBlock;
import com.freezephoenix.fabric.bettermod.api.block.TickingBetterBlock;
import com.freezephoenix.fabric.bettermod.impl.BetterMod;
import com.freezephoenix.fabric.bettermod.impl.block.entity.ResonantSculkSensorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.Nullable;

public class ResonantSculkSensorBlock extends SculkSensorBlock implements BetterBlock<ResonantSculkSensorBlockEntity>, TickingBetterBlock<ResonantSculkSensorBlockEntity> {
	private static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "resonant_sculk_sensor");
	public static final BlockItemId BlockItemID = BlockItemId.create(ID, ID);
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final MapCodec<ResonantSculkSensorBlock> CODEC = simpleCodec(ResonantSculkSensorBlock::new);

	@Override
	public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
		return new ResonantSculkSensorBlockEntity(worldPosition, blockState);
	}

	public ResonantSculkSensorBlock(final BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	public MapCodec<ResonantSculkSensorBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockEntityType<ResonantSculkSensorBlockEntity> getBlockEntityType() {
		return BetterMod.RESONANT_SCULK_SENSOR.entity();
	}

	@SuppressWarnings("DataFlowIssue")
	public BlockState getStateForPlacement(final BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection());
	}

	public int getSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
		return direction != state.getValue(FACING)
			   ? super.ownSignal(state, level, pos)
			   : 0;
	}

	protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	public BlockState rotate(final BlockState state, final Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(final BlockState state, final Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickingBetterBlock.super.getTicker(world, state, type);
	}

	public int getActiveTicks() {
		return 10;
	}
}
