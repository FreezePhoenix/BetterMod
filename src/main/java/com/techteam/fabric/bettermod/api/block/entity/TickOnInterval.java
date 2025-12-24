package com.techteam.fabric.bettermod.api.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public abstract class TickOnInterval<T extends BetterBlockEntity<T>> extends BetterBlockEntity<T> implements ITickable {
	private static final String COOLDOWN_ID = "TickOnInterval::cooldown";
	private int cooldown = 0;
	public long LAST_TICK;

	public TickOnInterval(BlockEntityType<T> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState, int size) {
		super(blockEntityType, blockPos, blockState, size);
	}

	/**
	 * Update the block entity. The block entity is responsible for calling setCooldown.
	 *
	 * @param world      The world the block entity is in.
	 * @param pos        The block globalPos.
	 * @param blockState The block state.
	 */
	public abstract void onCooldown(ServerWorld world, BlockPos pos, BlockState blockState);

	@Override
	public void writeData(WriteView view) {
		super.writeData(view);
		view.putInt(COOLDOWN_ID, cooldown);
	}

	@Override
	public void readData(ReadView view) {
		super.readData(view);
		this.cooldown = view.getInt(COOLDOWN_ID, 0);
	}

	public final void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	@Override
	public final void tick(ServerWorld world, BlockPos pos, BlockState blockState) {
		cooldown--;
		LAST_TICK = world.getTime();
		if (!(cooldown > 0)) {
			cooldown = 0;
			onCooldown(world, pos, blockState);
		}
	}
}
