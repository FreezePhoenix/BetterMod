package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.api.block.entity.TickOnInterval;
import com.techteam.fabric.bettermod.client.gui.HopperScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public abstract class BetterHopperBlockEntity<T extends BetterHopperBlockEntity<T>> extends TickOnInterval<T> {
	public BetterHopperBlockEntity(BlockEntityType<T> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState, 5, 8);
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new HopperScreenHandler(syncId, playerInventory, this.getInventory());
	}
}
