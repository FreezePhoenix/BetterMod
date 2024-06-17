package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.client.RoomTracker;
import com.techteam.fabric.bettermod.hooks.RenderHooks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class MixinEntity implements RenderHooks.IRoomCaching {
	@Shadow
	private World world;

	@Shadow public abstract UUID getUuid();

	@Shadow public abstract BlockPos getBlockPos();

	@Unique
	private RoomTracker.Room CURRENT_ROOM;
	@Unique
	private int stamp;

	@Inject(method = "setPos",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/world/entity/EntityChangeListener;updateEntityPosition()V"))
	public void onUpdateEntityPosition(double x, double y, double z, CallbackInfo ci) {
		if(this.forceRender()) {
			return;
		}
		if (this.world.isClient()) {
			if (CURRENT_ROOM == null) {
				CURRENT_ROOM = RoomTracker.getRoomForPos(this.betterMod$blockPos());
				if (CURRENT_ROOM != null) {
					if (BetterMod.CONFIG.LogRoomTransitions) {
						BetterMod.LOGGER.info("{} entering room: {}", this.getUuid(), CURRENT_ROOM.getUUID());
					}
					stamp = CURRENT_ROOM.getStamp();
				} else {
					stamp = RoomTracker.getNullRoomStamp();
				}
			} else {
				if (CURRENT_ROOM.contains(this.betterMod$blockPos())) {
					stamp = CURRENT_ROOM.getStamp();
				} else {
					if (BetterMod.CONFIG.LogRoomTransitions) {
						BetterMod.LOGGER.info("{} exiting room: {}", this.getUuid(), CURRENT_ROOM.getUUID());
					}
					CURRENT_ROOM = RoomTracker.getRoomForPos(this.betterMod$blockPos());
					if (CURRENT_ROOM != null) {
						if (BetterMod.CONFIG.LogRoomTransitions) {
							BetterMod.LOGGER.info("{} entering room: {}", this.getUuid(), CURRENT_ROOM.getUUID());
						}
						stamp = CURRENT_ROOM.getStamp();
					} else {
						stamp = RoomTracker.getNullRoomStamp();
					}
				}
			}
		}
	}

	@Unique
	@Override
	public int betterMod$getStamp() {
		return stamp;
	}

	@Unique
	@Override
	public void betterMod$setStamp(int stamp) {
		this.stamp = stamp;
	}

	@Unique
	@Override
	public RoomTracker.Room betterMod$getRoom() {
		return CURRENT_ROOM;
	}

	@Unique
	@Override
	public void betterMod$setRoom(RoomTracker.Room room) {
		this.CURRENT_ROOM = room;
	}

	@Unique
	@Override
	public BlockPos betterMod$blockPos() {
		return this.getBlockPos();
	}
}
