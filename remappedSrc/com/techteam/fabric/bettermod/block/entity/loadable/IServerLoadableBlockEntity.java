package com.techteam.fabric.bettermod.block.entity.loadable;

import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IServerLoadableBlockEntity {
    void onServerLoad(ServerWorld world, BlockPos pos, BlockState state);
    void onServerUnload(ServerWorld world, BlockPos pos, BlockState state);

    static void onLoad(BlockEntity blockEntity, ServerWorld world) {
        if (blockEntity instanceof IServerLoadableBlockEntity loadableBlockEntity) {
            loadableBlockEntity.onServerLoad(world, blockEntity.getPos(), blockEntity.getCachedState());
        }
    }
    static void onUnLoad(BlockEntity blockEntity, ServerWorld world) {
        if (blockEntity instanceof IServerLoadableBlockEntity loadableBlockEntity) {
            loadableBlockEntity.onServerUnload(world, blockEntity.getPos(), blockEntity.getCachedState());
        }
    }
}
