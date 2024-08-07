package com.techteam.fabric.bettermod.api.block.entity.loadable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface IServerLoadableBlockEntity {
	default void onServerLoad(ServerWorld world, BlockPos pos, BlockState state) {
	}

	default void onServerUnload(ServerWorld world, BlockPos pos, BlockState state) {
	}

	static void onLoad(BlockEntity blockEntity, ServerWorld world) {
		if (blockEntity instanceof IServerLoadableBlockEntity loadableBlockEntity) {
			loadableBlockEntity.onServerLoad(world, blockEntity.getPos(), blockEntity.getCachedState());
		}
	}

	static void onUnload(BlockEntity blockEntity, ServerWorld world) {
		if (blockEntity instanceof IServerLoadableBlockEntity loadableBlockEntity) {
			loadableBlockEntity.onServerUnload(world, blockEntity.getPos(), blockEntity.getCachedState());
		}
	}
}
