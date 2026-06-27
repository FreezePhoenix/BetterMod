package com.freezephoenix.fabric.bettermod.impl.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ITickable {
	// TODO: WIP
	void tick(Player player, ItemStack item);
}
