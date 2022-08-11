package com.techteam.fabric.bettermod.hooks;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.client.RoomTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;

@Environment(EnvType.CLIENT)
public final class RenderHooks {
	public static boolean shouldRenderEntity(final Entity entity) {
		final RoomTracker.Room currentRoom = RoomTracker.getActiveRoom();
		return entity instanceof ItemFrameEntity
			   ? currentRoom == null
				 ? RoomTracker.getRoomForEntity(entity) == null
				 : currentRoom.contains(entity.getPos())
			   : currentRoom == null || currentRoom.contains(
					   entity.getPos()) || RoomTracker.getRoomForEntity(entity) == null;
	}

	public static boolean shouldRenderTileEntity(final BlockEntity blockEntity) {
		if (blockEntity instanceof RoomControllerBlockEntity) {
			return true;
		}
		final RoomTracker.Room currentRoom = RoomTracker.getActiveRoom();
		return currentRoom == null
			   ? RoomTracker.getRoomForBlockEntity(blockEntity) == null
			   : currentRoom.contains(blockEntity.getPos());
	}
}
