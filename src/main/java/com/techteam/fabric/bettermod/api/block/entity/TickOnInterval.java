package com.techteam.fabric.bettermod.api.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class TickOnInterval<T extends BetterBlockEntity> extends BetterBlockEntity implements ITickable {
	private static final String DELAY_ID = "TickOnInterval::cooldown";
	private final int MAX_COOLDOWN;
	private int cooldown = 0;

	public TickOnInterval(BlockEntityType<T> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState, int size, int MAX_COOLDOWN) {
		super(blockEntityType, blockPos, blockState, size);
		this.MAX_COOLDOWN = MAX_COOLDOWN;
	}

	/**
	 * Update the block entity.
	 * @param world The world the block entity is in.
	 * @param pos The block globalPos.
	 * @param blockState  The block state.
	 * @return Whether the BE should go back on cooldown.
	 */

	public abstract boolean scheduledTick(World world, BlockPos pos, BlockState blockState);

	@Override
	public void writeNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putInt(DELAY_ID, cooldown);
		super.writeNbt(tag, registryLookup);
	}

	@Override
	public void readNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		this.cooldown = tag.getInt(DELAY_ID);
		super.readNbt(tag, registryLookup);
	}

	@Override
	public final void tick(World world, BlockPos pos, BlockState blockState) {
		if (!world.isClient()) {
			if (--cooldown > 0) {
				return;
			}
			cooldown = 0;
			if(scheduledTick(world, pos, blockState)) {
				cooldown = MAX_COOLDOWN;
			}
		}
	}
}
