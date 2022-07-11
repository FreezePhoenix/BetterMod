package com.techteam.fabric.bettermod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITickable {
	void tick(World world, BlockPos pos, BlockState blockState);

	static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockEntity) {
		if(blockEntity instanceof ITickable tickable) {
			tickable.tick(world, pos, state);
		}
	}
}
