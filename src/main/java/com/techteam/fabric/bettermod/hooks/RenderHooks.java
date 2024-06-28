package com.techteam.fabric.bettermod.hooks;

import com.techteam.fabric.bettermod.api.hooks.IRoomCaching;
import com.techteam.fabric.bettermod.client.RoomTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.LightBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;

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

}
