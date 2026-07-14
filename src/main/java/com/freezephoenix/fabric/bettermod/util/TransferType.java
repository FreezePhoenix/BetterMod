package com.freezephoenix.fabric.bettermod.util;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public enum TransferType {
	STANDARD {
		@Override
		public TransferResult handle(@Nullable Storage<ItemVariant> from, Storage<ItemVariant> middle, @Nullable Storage<ItemVariant> to) {
			boolean inserted = to != null && StorageUtil.move(middle, to, ALWAYS_TRUE, 1, null) == 1;
			boolean extracted = from != null && StorageUtil.move(from, middle,ALWAYS_TRUE, 1, null) == 1;
			return new TransferResult(extracted, inserted);
		}
	},
	STICKING {
		@Override
		public TransferResult handle(@Nullable Storage<ItemVariant> from, Storage<ItemVariant> middle, @Nullable Storage<ItemVariant> to) {
			boolean inserted = to == null; // if the destination is null, we can just say we have already inserted
			boolean extracted = from == null; // same for source

			for (StorageView<ItemVariant> view : middle.nonEmptyViews()) {
				ItemVariant resource = view.getResource();
				if(!extracted && !inserted) {
					// If we haven't inserted, and we haven't extracted, we can process items that can't be stacked.
					// We also do this for normal items, as we would prefer to skip over the middle inventory if possible.
					try (Transaction transferTransaction = Transaction.openOuter()) {
						if (from.extract(resource, 1, transferTransaction) == 1 && to.insert(resource, 1, transferTransaction) == 1) {
							transferTransaction.commit();
							inserted = true;
							extracted = true;
							break;
						}
					}
				} else if(view.getCapacity() > 1) {
					boolean safe_to_extract = view.getAmount() < view.getCapacity();
					if (safe_to_extract && !extracted) {
						try (Transaction transferTransaction = Transaction.openOuter()) {

							if (StorageUtil.tryInsertStacking(
									middle,
									resource,
									1,
									transferTransaction
							) == 1 && from.extract(
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
					if (safe_to_insert && !inserted) {
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
	public record TransferResult(boolean extracted, boolean inserted) {}
	private static final Predicate<ItemVariant> ALWAYS_TRUE = _ -> true;
	public abstract TransferResult handle(@Nullable Storage<ItemVariant> from, Storage<ItemVariant> middle, @Nullable Storage<ItemVariant> to);
}
