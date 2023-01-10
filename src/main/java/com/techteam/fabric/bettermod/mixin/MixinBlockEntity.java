package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.client.RoomTracker;
import com.techteam.fabric.bettermod.hooks.IRoomCaching;
import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntity.class)
public class MixinBlockEntity implements IRoomCaching {
	@Unique
	private RoomTracker.Room CURRENT_ROOM;
	@Unique
	private int stamp;

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
