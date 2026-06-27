package com.freezephoenix.fabric.bettermod.impl.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TransferType {
	STANDARD {
		@Override
		public TransferResult handle(@Nullable Storage<ItemVariant> from, @NotNull Storage<ItemVariant> middle, @Nullable Storage<ItemVariant> to) {
			boolean inserted = to != null && StorageUtil.move(middle, to, variant -> true, 1, null) == 1;
			boolean extracted = from != null && StorageUtil.move(from, middle, variant -> true, 1, null) == 1;
			return new TransferResult(extracted, inserted);
		}
	},
	STICKING {
		@Override
		public TransferResult handle(@Nullable Storage<ItemVariant> from, @NotNull Storage<ItemVariant> middle, @Nullable Storage<ItemVariant> to) {
			boolean inserted = to == null; // if the destination is null, we can just say we have already inserted
			boolean extracted = from == null; // same for source

			for (StorageView<ItemVariant> view : middle.nonEmptyViews()) {

				ItemVariant resource = view.getResource();
				if (view.getCapacity() <= 1) {
					// Non-stackable logic
					if(!inserted && !extracted) {
						try (Transaction transferTransaction = Transaction.openOuter()) {
							if (to.insert(resource, 1, transferTransaction) == 1 && from.extract(
									resource,
									1,
									transferTransaction
							) == 1) {
								transferTransaction.commit();
								inserted = true;
								extracted = true;
								break;
							}
						}
					}
				} else {
					// Stackable logic
					if(!extracted && !inserted) {
						// Prefer skipping the middle inventory completely.
						try (Transaction transferTransaction = Transaction.openOuter()) {
							if (to.insert(resource, 1, transferTransaction) == 1 && from.extract(resource, 1, transferTransaction) == 1) {
								transferTransaction.commit();
								inserted = true;
								extracted = true;
								break;
							}
						}
					}
					boolean safe_to_extract = view.getAmount() < view.getCapacity();
					if(safe_to_extract && !extracted) {
						try (Transaction transferTransaction = Transaction.openOuter()) {

							if (StorageUtil.tryInsertStacking(middle, resource, 1, transferTransaction) == 1 && from.extract(
									resource,
									1,
									transferTransaction
							) == 1) {
								transferTransaction.commit();
								extracted = true;
							}
						}
					}
					boolean safe_to_insert = view.getAmount() > 1;
					if(safe_to_insert && !inserted) {
						try (Transaction transferTransaction = Transaction.openOuter()) {
							if (to.insert(resource, 1, transferTransaction) == 1 && view.extract(
									resource,
									1,
									transferTransaction
							) == 1) {
								transferTransaction.commit();
								inserted = true;
							}
						}
					}
				}
				if(inserted && extracted) {
					break;
				}
			}
			return new TransferResult(from != null && extracted, to != null && inserted);
		}
	};
	public record TransferResult(boolean extracted, boolean inserted) {};


	public abstract TransferResult handle(@Nullable Storage<ItemVariant> from, @NotNull Storage<ItemVariant> middle, @Nullable Storage<ItemVariant> to);
}
