package com.techteam.fabric.bettermod.client.gui;

import com.techteam.fabric.bettermod.BetterMod;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import org.jetbrains.annotations.NotNull;

public final class BitHopperScreenHandler extends SyncedGuiDescription {
	private static final int INVENTORY_SIZE = 5;

	public BitHopperScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull PacketByteBuf buf) {
		this(syncId, playerInventory, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos()));
	}

	public BitHopperScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull ScreenHandlerContext context) {
		super(
				BetterMod.BIT_HOPPER_SCREEN_HANDLER_TYPE,
				syncId,
				playerInventory,
				getBlockInventory(context, INVENTORY_SIZE),
				getBlockPropertyDelegate(context)
		);

		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);
		root.setSize(176, 150);
		WItemSlot slot = WItemSlot.of(blockInventory, 0, 5, 1);
		root.add(slot, 16, 17);
		root.add(this.createPlayerInventoryPanel(), 7, 56);
		root.validate(this);
	}
}