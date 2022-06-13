package com.techteam.fabric.bettermod.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public final class BetterBookshelfScreen extends CottonInventoryScreen<BetterBookshelfScreenHandler> {
    public BetterBookshelfScreen(@NotNull BetterBookshelfScreenHandler description, @NotNull PlayerInventory inventory, Text title) {
        super(description, inventory, title);
    }
}
