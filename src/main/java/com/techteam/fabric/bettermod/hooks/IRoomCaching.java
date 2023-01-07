package com.techteam.fabric.bettermod.hooks;

import com.techteam.fabric.bettermod.client.RoomTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IRoomCaching {
	RoomTracker.Room getRoom();
}
