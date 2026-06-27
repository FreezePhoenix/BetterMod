package com.freezephoenix.fabric.bettermod.api.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickable {
	void tick(ServerLevel world, BlockPos pos, BlockState blockState);
}
