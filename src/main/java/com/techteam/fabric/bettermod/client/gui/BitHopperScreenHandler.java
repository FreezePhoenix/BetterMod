package com.techteam.fabric.bettermod.client.gui;

import com.techteam.fabric.bettermod.BetterMod;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class BitHopperScreenHandler extends HopperScreenHandler {
	public BitHopperScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull BlockPos pos) {
		this(syncId, playerInventory, ScreenHandlerContext.create(playerInventory.player.getWorld(), pos));
	}

	public BitHopperScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull ScreenHandlerContext context) {
		super(
				BetterMod.BIT_HOPPER_SCREEN_HANDLER_TYPE,
				syncId,
				playerInventory,
				context
		);
	}
}