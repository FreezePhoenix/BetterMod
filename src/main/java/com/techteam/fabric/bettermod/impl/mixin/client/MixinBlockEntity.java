package com.techteam.fabric.bettermod.impl.mixin.client;

import com.techteam.fabric.bettermod.api.hooks.IRoomCaching;
import com.techteam.fabric.bettermod.impl.client.RoomTracker;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntity implements IRoomCaching {
	@Shadow
	public abstract BlockPos getPos();

	@Unique
	private RoomTracker.Room CURRENT_ROOM;
	@Unique
	private int stamp;

	@Unique
	@Override
	public int betterMod$getStamp() {
		return stamp;
	}

	@Unique
	@Override
	public void betterMod$setStamp(int stamp) {
		this.stamp = stamp;
	}

	@Unique
	@Override
	public RoomTracker.Room betterMod$getRoom() {
		return CURRENT_ROOM;
	}

	@Unique
	@Override
	public void betterMod$setRoom(RoomTracker.Room room) {
		this.CURRENT_ROOM = room;
	}

	@Unique
	@Override
	public BlockPos betterMod$blockPos() {
		return this.getPos();
	}
}
