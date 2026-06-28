package com.freezephoenix.fabric.bettermod.api.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class TickOnInterval<T extends BetterBlockEntity<T>> extends BetterBlockEntity<T> implements ITickable {
	private static final String COOLDOWN_ID = "TickOnInterval::cooldown";
	private int cooldown = 0;
	public long LAST_TICK;

	public TickOnInterval(BlockEntityType<T> blockEntityType, BlockPos blockPos, BlockState blockState, int size) {
		super(blockEntityType, blockPos, blockState, size);
	}

	/**
	 * Update the block entity. The block entity is responsible for calling setCooldown.
	 *
	 * @param world      The world the block entity is in.
	 * @param pos        The block globalPos.
	 * @param blockState The block state.
	 */
	public abstract void onCooldown(ServerLevel world, BlockPos pos, BlockState blockState);

	@Override
	public void saveAdditional(ValueOutput view) {
		super.saveAdditional(view);
		view.putInt(COOLDOWN_ID, cooldown);
	}

	@Override
	public void loadAdditional(ValueInput view) {
		super.loadAdditional(view);
		this.cooldown = view.getIntOr(COOLDOWN_ID, 0);
	}

	public final void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	@Override
	public final void tick(ServerLevel world, BlockPos pos, BlockState blockState) {
		cooldown--;
		LAST_TICK = world.getGameTime();
		if (!(cooldown > 0)) {
			cooldown = 0;
			onCooldown(world, pos, blockState);
		}
	}
}
