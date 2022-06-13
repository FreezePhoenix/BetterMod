package com.techteam.fabric.bettermod.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public final class RoomControllerScreen extends CottonInventoryScreen<RoomControllerScreenHandler> {
    public RoomControllerScreen(@NotNull RoomControllerScreenHandler description, @NotNull PlayerInventory inventory, Text title) {
        super(description, inventory, title);
    }
}
