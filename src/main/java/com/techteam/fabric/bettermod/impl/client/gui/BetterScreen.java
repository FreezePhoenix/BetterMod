package com.techteam.fabric.bettermod.impl.client.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class BetterScreen<T extends SyncedGuiDescription> extends CottonInventoryScreen<T> {
	public BetterScreen(@NotNull T description, @NotNull Inventory inventory, Component title) {
		super(description, inventory, title);
	}
}
