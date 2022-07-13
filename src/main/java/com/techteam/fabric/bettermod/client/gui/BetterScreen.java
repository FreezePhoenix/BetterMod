package com.techteam.fabric.bettermod.client.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public final class BetterScreen<T extends SyncedGuiDescription> extends CottonInventoryScreen<T> {
    public BetterScreen(@NotNull T description, @NotNull PlayerInventory inventory, Text title) {
        super(description, inventory, title);
    }
    public static <T extends SyncedGuiDescription> BetterScreen<T> create(@NotNull T description, @NotNull PlayerInventory inventory, Text title) {
        return new BetterScreen<>(description, inventory, title);
    }
}
