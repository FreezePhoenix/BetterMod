package com.techteam.fabric.bettermod.impl.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

public enum TransferType {
	STANDARD {
		@Override
		public boolean handle(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
			return StorageUtil.move(from, to, variant -> true, 1, null) == 1;
		}
	},
	STACKING {
		@Override
		public boolean handle(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
			return StorageUtil.move(from, to, variant -> variant.toStack().isStackable(), 1, null) == 1;
		}
	},
	STICKING {
		@Override
		public boolean handle(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to) {
			for (StorageView<ItemVariant> view : from.nonEmptyViews()) {
				if (view.getCapacity() <= 1 || view.getAmount() == 1) {
					continue;
				}
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
			}
			return false;
		}
	};

	public abstract boolean handle(@NotNull Storage<ItemVariant> from, @NotNull Storage<ItemVariant> to);
}
