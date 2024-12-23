package com.techteam.fabric.bettermod.impl.mixin.lithium;

import com.techteam.fabric.bettermod.api.block.entity.TickOnInterval;
import net.caffeinemc.mods.lithium.common.block.entity.SleepingBlockEntity;
import net.caffeinemc.mods.lithium.mixin.world.block_entity_ticking.sleeping.WrappedBlockEntityTickInvokerAccessor;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TickOnInterval.class)
public class TickOnIntervalMixin implements SleepingBlockEntity {
	@Unique
	private WrappedBlockEntityTickInvokerAccessor tickWrapper = null;
	@Unique
	private BlockEntityTickInvoker sleepingTicker = null;
	@Shadow(remap = false)
	public long LAST_TICK;

	@Inject(
			method = "setCooldown",
			at = @At("HEAD"),
			remap = false
	)
	public void setCooldownHook(int cooldown, boolean special, CallbackInfo callbackInfo) {
		if (special) {
			if (this.LAST_TICK == Long.MAX_VALUE) {
				this.sleepOnlyCurrentTick();
			} else {
				this.wakeUpNow();
			}
		} else if (cooldown > 0 && this.sleepingTicker != null) {
			this.wakeUpNow();
		}
	}

	public WrappedBlockEntityTickInvokerAccessor lithium$getTickWrapper() {
		return this.tickWrapper;
	}

	public void lithium$setTickWrapper(WrappedBlockEntityTickInvokerAccessor tickWrapper) {
		this.tickWrapper = tickWrapper;
		this.lithium$setSleepingTicker(null);
	}

	public BlockEntityTickInvoker lithium$getSleepingTicker() {
		return this.sleepingTicker;
	}

	public void lithium$setSleepingTicker(BlockEntityTickInvoker sleepingTicker) {
		this.sleepingTicker = sleepingTicker;
	}

	public boolean lithium$startSleeping() {
		if (this.isSleeping()) {
			return false;
		} else {
			WrappedBlockEntityTickInvokerAccessor tickWrapper = this.lithium$getTickWrapper();
			if (tickWrapper != null) {
				this.lithium$setSleepingTicker(tickWrapper.getWrapped());
				tickWrapper.callSetWrapped(SleepingBlockEntity.SLEEPING_BLOCK_ENTITY_TICKER);
				this.LAST_TICK = Long.MAX_VALUE;
				return true;
			} else {
				return false;
			}
		}
	}
}
