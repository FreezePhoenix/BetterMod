package com.techteam.fabric.bettermod.api.block.entity;

import me.jellysquid.mods.lithium.common.block.entity.SleepingBlockEntity;
import me.jellysquid.mods.lithium.mixin.world.block_entity_ticking.sleeping.WrappedBlockEntityTickInvokerAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class TickOnInterval<T extends BetterBlockEntity> extends BetterBlockEntity implements ITickable {
	private static final String DELAY_ID = "TickOnInterval::cooldown";
	protected final int MAX_COOLDOWN;
	public int cooldown = 0;
	public long LAST_TICK;

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

	public abstract void scheduledTick(World world, BlockPos pos, BlockState blockState);

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

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	@Override
	public final void tick(World world, BlockPos pos, BlockState blockState) {
		if (!world.isClient()) {
			--cooldown;
			LAST_TICK = world.getTime();
			if (!(cooldown > 0)) {
				cooldown = 0;
				scheduledTick(world, pos, blockState);
			}
		}
	}
}
