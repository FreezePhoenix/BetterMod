package com.techteam.fabric.bettermod.util;

import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class InventoryUtil {
	public static Inventory getBlockInventory(ScreenHandlerContext ctx, int size) {
		return getBlockInventory(ctx, () -> new SimpleInventory(size));
	}

	private static Inventory getBlockInventory(ScreenHandlerContext ctx, Supplier<Inventory> fallback) {
		return ctx.get((world, pos) -> {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof BetterBlockEntity blockEntity) {
				return blockEntity.getInventory();
			}

			return null;
		}).orElseGet(fallback);
	}

	public static boolean handleTransfer(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
		for (StorageView<ItemVariant> view : from) {
			if (view.isResourceBlank()) continue;
			if(handle(to, view)) {
				return true;
			}
		}
		return false;
	}

	public static boolean handleTransferSticky(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
		for (StorageView<ItemVariant> view : from) {
			if (view.isResourceBlank() || view.getAmount() <= 1) {
				continue;
			}
			if(handle(to, view)) {
				return true;
			}
		}
		return false;
	}

	public static boolean handleTransferStackable(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
		for (StorageView<ItemVariant> view : from) {
			if (view.isResourceBlank() || view.getCapacity() <= 1) {
				continue;
			}
			if(handle(to, view)) {
				return true;
			}
		}
		return false;
	}

	private static boolean handle(Storage<ItemVariant> to, StorageView<ItemVariant> view) {
		ItemVariant resource = view.getResource();
		try (Transaction transferTransaction = Transaction.openOuter()) {
			if (to.insert(resource, 1, transferTransaction) == 1 && view.extract(resource, 1, transferTransaction) == 1) {
				transferTransaction.commit();
				return true;
			}
		}
		return false;
	}
}
