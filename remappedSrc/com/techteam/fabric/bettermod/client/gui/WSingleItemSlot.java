package com.techteam.fabric.bettermod.impl.client.gui;

import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class WSingleItemSlot extends WItemSlot {
    protected int ind;
    @Environment(EnvType.CLIENT)
    // TODO: Set the background painter to SLOT in a new method that sets a widget's
    // default painter.
    protected Inventory inv;

    public WSingleItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big) {
        super(inventory, startIndex, slotsWide, slotsHigh, big);
        inv = inventory;
        ind = startIndex;
    }

    public static @NotNull WSingleItemSlot of(Inventory inventory, int index) {
        return new WSingleItemSlot(inventory, index, 1, 1, false);
    }

    public static @NotNull WSingleItemSlot of(Inventory inventory, int startIndex, int slotsWide, int slotsHigh) {
        return new WSingleItemSlot(inventory, startIndex, slotsWide, slotsHigh, false);
    }

    /**
     * Creates a 9x3 slot widget from the "main" part of a player inventory.
     *
     * @param inventory the player inventory
     * @return the created slot widget
     * @see WPlayerInvPanel
     */
    public static @NotNull WSingleItemSlot ofPlayerStorage(Inventory inventory) {
        return new WSingleItemSlot(inventory, 9, 9, 3, false);
    }

    public static @NotNull WSingleItemSlot outputOf(Inventory inventory, int index) {
        return new WSingleItemSlot(inventory, index, 1, 1, true);
    }
}
