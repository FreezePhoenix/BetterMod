package com.techteam.fabric.bettermod.impl.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITickable {
	// TODO: WIP
	void tick(PlayerEntity player, ItemStack item);
}
