package com.techteam.fabric.bettermod.api.block.entity.loadable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public interface IClientLoadableBlockEntity {
	default void onClientLoad(World world, BlockPos pos, BlockState state) {
	}

	default void onClientUnload(World world, BlockPos pos, BlockState state) {
	}

	static void onLoad(BlockEntity blockEntity, ClientWorld world) {
		// Unfortunately this event is triggered before the block entity actually reads its NBT...
		if (blockEntity instanceof IClientLoadableBlockEntity loadableBlockEntity) {
			loadableBlockEntity.onClientLoad(world, blockEntity.getPos(), blockEntity.getCachedState());
		}
	}

	static void onUnload(BlockEntity blockEntity, ClientWorld world) {
		if (blockEntity instanceof IClientLoadableBlockEntity loadableBlockEntity) {
			loadableBlockEntity.onClientUnload(world, blockEntity.getPos(), blockEntity.getCachedState());
		}
	}
}
