package com.techteam.fabric.bettermod.impl.client.gui;

import com.techteam.fabric.bettermod.impl.BetterMod;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.NotNull;

public class HopperScreenHandler extends SyncedGuiDescription {
	private static final int INVENTORY_SIZE = 5;

	public HopperScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {

		super(BetterMod.HOPPER_SCREEN_HANDLER_TYPE, syncId, playerInventory, inventory, null);
		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);
		root.setSize(176, 131);
		WItemSlot slot = WItemSlot.of(blockInventory, 0, 5, 1);
		// Slots are 18 units wide as an item texture is 16 pixels, and there is 1
		// pixel on either side.
		// 5 slots = 90 units, if the GUI is 176 units wide, that
		// leaves us 86 units to be split between the two sides, or 43 units on each side.
		root.add(slot, 43, 17);
		// This should match the hopper GUI
		root.add(this.createPlayerInventoryPanel(), 7, 37);
		root.validate(this);
	}

	public HopperScreenHandler(int syncId, @NotNull PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(INVENTORY_SIZE));
	}
}
