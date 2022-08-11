package com.techteam.fabric.bettermod.block.entity.loadable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IServerLoadableBlockEntity {
    void onServerLoad(World world, BlockPos pos, BlockState state);
    void onServerUnload(World world, BlockPos pos, BlockState state);
}
