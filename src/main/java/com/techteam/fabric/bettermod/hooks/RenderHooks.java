package com.techteam.fabric.bettermod.hooks;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.client.RoomTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class RenderHooks {
	public static boolean shouldRenderEntity(final Entity entity) {
		IRoomCaching cache = (IRoomCaching) entity;
		if(cache.forceRender()) {
			return true;
		}
		RoomTracker.Room currentRoom = RoomTracker.getActiveRoom();
		if(cache instanceof ItemFrameEntity) {
			if(currentRoom == null) {
				return RoomTracker.getRoom(cache) == null;
			}
			return currentRoom.contains(cache.blockPos());
		}
		return currentRoom == null || currentRoom.contains(cache.blockPos()) || RoomTracker.getRoom(cache) == null;
	}

	public static boolean shouldRenderTileEntity(final BlockEntity blockEntity) {
		IRoomCaching cache = (IRoomCaching) blockEntity;
		if(cache.forceRender()) {
			return true;
		}
		RoomTracker.Room currentRoom = RoomTracker.getActiveRoom();
		if(currentRoom == null) {
			return RoomTracker.getRoom(cache) == null;
		} else {
			return currentRoom.contains(cache.blockPos());
		}
	}

	@Environment(EnvType.CLIENT)
	public static interface IRoomCaching extends IForceRender {
		BlockPos blockPos();
		int getStamp();
		void setStamp(int stamp);
		void setRoom(RoomTracker.Room room);
		RoomTracker.Room getRoom();
	}
	public static interface IForceRender {
		public default boolean forceRender() {
			return false;
		}
	}
}
