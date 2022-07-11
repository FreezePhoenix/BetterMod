package com.techteam.fabric.bettermod.util;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class InventoryUtil {
	public static Inventory getInventoryAt(World world, BlockPos pos) {
		BlockEntity blockEntity;
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof InventoryProvider inventoryProvider) {
			return inventoryProvider.getInventory(blockState, world, pos);
		} else if (blockState.hasBlockEntity()) {
			blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity && block instanceof ChestBlock chestBlock) {
				return ChestBlock.getInventory(chestBlock, blockState, world, pos, true);
			}
			if (blockEntity instanceof Inventory inventory) {
				return inventory;
			}
		}
		return null;
	}
	public static SingleSlotStorage<ItemVariant> getFirstTransferrableSlotCap(InventoryStorage from, InventoryStorage to, TransactionContext transactionContext) {
		try(Transaction transaction = Transaction.openNested(transactionContext)) {
			for(SingleSlotStorage<ItemVariant> slot : from.getSlots()) {
				if(!(slot.getAmount() == 0) && canInsertExtract(from, to, slot, transaction)) {
					return slot;
				}
			}
		}
		return null;
	}

	/**
	 * Can we move an item from the inventory {from} at slot {slot} to the inventory {to}?
	 * @param from
	 * @param to
	 * @param slot
	 * @return
	 */
	private static boolean canInsertExtract(InventoryStorage from, InventoryStorage to, SingleSlotStorage<ItemVariant> slot, TransactionContext context) {
		try(Transaction transaction = Transaction.openNested(context)) {
			var itemExtract = slot.simulateExtract(slot.getResource(),1,transaction);
			if(itemExtract > 0) {
				var insert = to.simulateInsert(slot.getResource(), itemExtract, transaction);
				if(insert > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public static void handleTransfer(InventoryStorage from, InventoryStorage to) {
		try(Transaction transaction = Transaction.openOuter()) {
			SingleSlotStorage<ItemVariant> slot = getFirstTransferrableSlotCap(from, to, transaction);
			if(slot == null) {
				return;
			}
			var resource = slot.getResource();
			slot.extract(resource, 1, transaction);
			to.insert(resource, 1, transaction);
			transaction.commit();
		}
	}
}
