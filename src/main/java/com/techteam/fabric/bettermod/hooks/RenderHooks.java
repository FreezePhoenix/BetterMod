package com.techteam.fabric.bettermod.hooks;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.client.RoomTracker.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;

import static com.techteam.fabric.bettermod.client.RoomTracker.*;

@Environment(EnvType.CLIENT)
public final class RenderHooks {
	public static boolean shouldRenderEntity(final Entity entity) {
		final Room currentRoom = getActiveRoom();
		return entity instanceof ItemFrameEntity
			   ? currentRoom == null
				 ? getRoomForEntity(entity) == null
				 : currentRoom.contains(entity.getPos())
			   : currentRoom == null || currentRoom.contains(
					   entity.getPos()) || getRoomForEntity(entity) == null;
	}

	public static boolean shouldRenderTileEntity(final BlockEntity blockEntity) {

		if (blockEntity instanceof RoomControllerBlockEntity) {
			return true;
		}
		final Room currentRoom = getActiveRoom();
		return currentRoom == null
			   ? getRoomForBlockEntity(blockEntity) == null
			   : currentRoom.contains(blockEntity.getPos());
	}
}
