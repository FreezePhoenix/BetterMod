package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.client.RoomTracker;
import com.techteam.fabric.bettermod.hooks.RenderHooks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntity implements RenderHooks.IRoomCaching {
	@Shadow public abstract BlockPos getPos();
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
	@Unique
	@Override
	public BlockPos blockPos() {
		return this.getPos();
	}
}
