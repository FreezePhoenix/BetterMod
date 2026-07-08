package com.freezephoenix.fabric.bettermod.impl.block.entity;

import com.freezephoenix.fabric.bettermod.api.block.entity.TickingBetterBlockEntity;
import com.freezephoenix.fabric.bettermod.impl.BetterMod;
import com.freezephoenix.fabric.bettermod.impl.block.ResonantSculkSensorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jspecify.annotations.Nullable;

public class ResonantSculkSensorBlockEntity extends SculkSensorBlockEntity implements TickingBetterBlockEntity {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "resonant_sculk_sensor");

	public ResonantSculkSensorBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
		super(BetterMod.RESONANT_SCULK_SENSOR.entity(), worldPosition, blockState);
	}

	public VibrationSystem.User createVibrationUser() {
		return new VibrationUser(getBlockPos());
	}

	@Override
	public void tick(ServerLevel world, BlockPos pos, BlockState blockState) {
		VibrationSystem.Ticker.tick(
				world,
				getVibrationData(),
				getVibrationUser()
		);
	}

	protected class VibrationUser extends SculkSensorBlockEntity.VibrationUser {
		public VibrationUser(final BlockPos blockPos) {
			super(blockPos);
		}

		public int getListenerRadius() {
			return 32;
		}

		public boolean canReceiveVibration(final ServerLevel level, final BlockPos pos, final Holder<GameEvent> event, final GameEvent.@Nullable Context context) {
			int comparisonType = this.getBackSignal(level, this.blockPos, ResonantSculkSensorBlockEntity.this.getBlockState());
			return (comparisonType == 0 || VibrationSystem.getGameEventFrequency(event) == comparisonType) && super.canReceiveVibration(
					level,
					pos,
					event,
					context
			);
		}

		private int getBackSignal(final Level level, final BlockPos pos, final BlockState state) {
			Direction direction = state.getValue(ResonantSculkSensorBlock.FACING).getOpposite();
			return level.getSignal(pos.relative(direction), direction);
		}
	}
}

