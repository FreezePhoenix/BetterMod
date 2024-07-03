package com.techteam.fabric.bettermod.impl.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ITickable {
	// TODO: WIP
	void tick(PlayerEntity player, ItemStack item);
}
