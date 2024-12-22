package com.techteam.fabric.bettermod.impl.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

public class InventoryUtil {
	public static boolean handleTransfer(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
		for (StorageView<ItemVariant> view : from) {
			if (view.isResourceBlank()) continue;
			if (handle(to, view)) {
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
			if (handle(to, view)) {
				return true;
			}
		}
		return false;
	}

	private static boolean handle(Storage<ItemVariant> to, StorageView<ItemVariant> view) {
		ItemVariant resource = view.getResource();
		try (Transaction transferTransaction = Transaction.openOuter()) {
			if (to.insert(resource, 1, transferTransaction) == 1 && view.extract(
					resource,
					1,
					transferTransaction
			) == 1) {
				transferTransaction.commit();
				return true;
			}
		}
		return false;
	}
}
