package com.freezephoenix.fabric.bettermod.impl.client.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public final class BetterScreen<T extends SyncedGuiDescription> extends CottonInventoryScreen<T> {
	public BetterScreen(T description, Inventory inventory, Component title) {
		super(description, inventory, title);
	}
}
