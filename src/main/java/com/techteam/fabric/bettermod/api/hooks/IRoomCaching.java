package com.techteam.fabric.bettermod.api.hooks;

import com.techteam.fabric.bettermod.client.RoomTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public interface IRoomCaching extends IForceRender {
	BlockPos betterMod$blockPos();

	int betterMod$getStamp();

	void betterMod$setStamp(int stamp);

	void betterMod$setRoom(RoomTracker.Room room);

	RoomTracker.Room betterMod$getRoom();
}
