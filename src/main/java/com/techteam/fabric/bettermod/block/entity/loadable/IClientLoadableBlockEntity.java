package com.techteam.fabric.bettermod.block.entity.loadable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClientLoadableBlockEntity {
    @Environment(EnvType.CLIENT)
    void onClientLoad(World world, BlockPos pos, BlockState state);
    @Environment(EnvType.CLIENT)
    void onClientUnload(World world, BlockPos pos, BlockState state);
}
