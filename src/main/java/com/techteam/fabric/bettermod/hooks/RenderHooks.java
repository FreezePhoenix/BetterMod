package com.techteam.fabric.bettermod.hooks;

import com.techteam.fabric.bettermod.client.RoomTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class RenderHooks {
	public static boolean shouldRenderEntity(final Entity entity) {
		IRoomCaching cache = (IRoomCaching) entity;
		RoomTracker.Room currentRoom = RoomTracker.getActiveRoom();
		return cache.forceRender() || (cache instanceof ItemFrameEntity && (currentRoom == null && RoomTracker.getOrUpdateRoom(
				cache) == null || currentRoom != null && currentRoom.contains(cache.betterMod$blockPos())) || !(cache instanceof ItemFrameEntity) && (currentRoom == null || currentRoom.contains(
				cache.betterMod$blockPos()) || RoomTracker.getOrUpdateRoom(
				cache) == null));
	}

	public static boolean shouldRenderTileEntity(final BlockEntity blockEntity) {
		IRoomCaching cache = (IRoomCaching) blockEntity;
		if(cache.forceRender()) {
			return true;
		}
		RoomTracker.Room currentRoom = RoomTracker.getActiveRoom();
		if(currentRoom == null) {
			return RoomTracker.getOrUpdateRoom(cache) == null;
		} else {
			return currentRoom.contains(cache.betterMod$blockPos());
		}
	}

	@Environment(EnvType.CLIENT)
	public interface IRoomCaching extends IForceRender {
		BlockPos betterMod$blockPos();
		int betterMod$getStamp();
		void betterMod$setStamp(int stamp);
		void betterMod$setRoom(RoomTracker.Room room);
		RoomTracker.Room betterMod$getRoom();
	}
	public interface IForceRender {
		default boolean forceRender() {
			return false;
		}
	}
}
