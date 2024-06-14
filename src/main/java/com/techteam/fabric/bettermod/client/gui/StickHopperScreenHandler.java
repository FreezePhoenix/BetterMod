package com.techteam.fabric.bettermod.client.gui;

import com.techteam.fabric.bettermod.BetterMod;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import org.jetbrains.annotations.NotNull;

public final class StickHopperScreenHandler extends HopperScreenHandler {
	public StickHopperScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull PacketByteBuf buf) {
		this(syncId, playerInventory, ScreenHandlerContext.create(playerInventory.player.getWorld(), buf.readBlockPos()));
	}

	public StickHopperScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull ScreenHandlerContext context) {
		super(
				BetterMod.STICK_HOPPER_SCREEN_HANDLER_TYPE,
				syncId,
				playerInventory,
				context
		);
	}
}