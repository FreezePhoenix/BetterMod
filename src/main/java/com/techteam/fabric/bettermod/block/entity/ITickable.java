package com.techteam.fabric.bettermod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITickable {
	void tick(World world, BlockPos pos, BlockState blockState);
}
