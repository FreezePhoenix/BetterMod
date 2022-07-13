package com.techteam.fabric.bettermod.client.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HopperScreenHandler extends SyncedGuiDescription {
	private static final int INVENTORY_SIZE = 5;
	public HopperScreenHandler(ScreenHandlerType<?>type, int syncId, PlayerInventory playerInventory, @NotNull ScreenHandlerContext context) {
		super(type, syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context));
		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);
		root.setSize(176, 131);
		WItemSlot slot = WItemSlot.of(blockInventory, 0, 5, 1);
		// Slots are 18 units wide as an item texture (flat) is 16 pixels, and there are 2
		// pixels on either side.
		// Slots are 18 units wide. 5 slots = 90 units, if the GUI is 176 units wide, that
		// leaves us 86 units to be split between the two sides, or 43 units on each side.
		root.add(slot, 43, 17);
		// This should match the hopper GUI
		root.add(this.createPlayerInventoryPanel(), 7, 37);
		root.validate(this);
	}
}
