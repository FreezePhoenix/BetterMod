package com.techteam.fabric.bettermod.api.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;

public abstract class TickOnInterval<T extends BetterBlockEntity> extends BetterBlockEntity implements ITickable {
	private static final String COOLDOWN_ID = "TickOnInterval::cooldown";
	public int cooldown = 0;
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
	public abstract void scheduledTick(World world, BlockPos pos, BlockState blockState);

	@Override
	public void writeNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putInt(COOLDOWN_ID, cooldown);
		super.writeNbt(tag, registryLookup);
	}

	@Override
	public void readNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		this.cooldown = tag.getInt(COOLDOWN_ID, 0);
		super.readNbt(tag, registryLookup);
	}

	public void setCooldown(int cooldown, boolean special) {
		this.cooldown = cooldown;
	}

	@Override
	public final void tick(World world, BlockPos pos, BlockState blockState) {
		if (!world.isClient()) {
			cooldown--;
			LAST_TICK = world.getTime();
			if (!(cooldown > 0)) {
				cooldown = 0;
				scheduledTick(world, pos, blockState);
			}
		}
	}
}
