package com.techteam.fabric.bettermod.api.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITickable {
	void tick(ServerWorld world, BlockPos pos, BlockState blockState);
}
