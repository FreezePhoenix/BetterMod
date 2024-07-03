package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IServerLoadableBlockEntity;
import com.techteam.fabric.bettermod.impl.util.InventoryUtil;
import com.techteam.fabric.bettermod.impl.util.Texts;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class StickHopperBlockEntity extends BetterExtractingHopperBlockEntity<StickHopperBlockEntity> {
	public static final Identifier ID = Identifier.of("bettermod", "stick_hopper");

	public StickHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.STICK_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
		if(stack.getCount() <= 1) {
			return false;
		}
		return super.canTransferTo(hopperInventory, slot, stack);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if(stack.getMaxCount() <= 1) {
			return false;
		}
		return super.isValid(slot, stack);
	}

	@Override
	public Text getContainerName() {
		return Texts.STICK_HOPPER;
	}
}
