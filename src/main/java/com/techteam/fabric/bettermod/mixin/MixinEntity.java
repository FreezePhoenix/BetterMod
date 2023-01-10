package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.client.RoomTracker;
import com.techteam.fabric.bettermod.hooks.IRoomCaching;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class MixinEntity implements IRoomCaching {
	@Shadow
	public World world;
	@Unique
	private RoomTracker.Room CURRENT_ROOM;
	@Unique
	private int stamp;

	@Shadow public abstract BlockPos getBlockPos();

	@Shadow public abstract UUID getUuid();

	@Inject(method = "setPos",
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityChangeListener;updateEntityPosition()V"))
	public void onUpdateEntityPosition(double x, double y, double z, CallbackInfo ci) {
		if (this.world.isClient()) {
			if (CURRENT_ROOM == null) {
				CURRENT_ROOM = RoomTracker.getRoomForPos(this.getBlockPos());
				if (CURRENT_ROOM != null) {
					if (BetterMod.CONFIG.LogRoomTransitions) {
						BetterMod.LOGGER.info(this.getUuid() + " entering room: " + CURRENT_ROOM.getUUID());
					}
					stamp = CURRENT_ROOM.getVersion();
				} else {
					stamp = RoomTracker.getNullRoomStamp();
				}
			} else {
				if (CURRENT_ROOM.contains(this.getBlockPos())) {
					stamp = CURRENT_ROOM.getVersion();
				} else {
					if (BetterMod.CONFIG.LogRoomTransitions) {
						BetterMod.LOGGER.info(this.getUuid() + " exiting room: " + CURRENT_ROOM.getUUID());
					}
					CURRENT_ROOM = RoomTracker.getRoomForPos(this.getBlockPos());
					if (CURRENT_ROOM != null) {
						if (BetterMod.CONFIG.LogRoomTransitions) {
							BetterMod.LOGGER.info(this.getUuid() + " entering room: " + CURRENT_ROOM.getUUID());
						}
						stamp = CURRENT_ROOM.getVersion();
					} else {
						stamp = RoomTracker.getNullRoomStamp();
					}
				}
			}
		}
	}

	@Unique
	@Override
	public int getStamp() {
		return stamp;
	}

	@Unique
	@Override
	public void setStamp(int stamp) {
		this.stamp = stamp;
	}

	@Unique
	@Override
	public RoomTracker.Room getRoom() {
		return CURRENT_ROOM;
	}

	@Unique
	@Override
	public void setRoom(RoomTracker.Room room) {
		this.CURRENT_ROOM = room;
	}
}
