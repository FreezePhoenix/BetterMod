package com.techteam.fabric.bettermod.impl.mixin.lithium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.techteam.fabric.bettermod.impl.block.entity.BetterExtractingHopperBlockEntity;
import com.techteam.fabric.bettermod.impl.block.entity.PullHopperBlockEntity;
import me.jellysquid.mods.lithium.api.inventory.LithiumInventory;
import me.jellysquid.mods.lithium.common.block.entity.inventory_change_tracking.InventoryChangeTracker;
import me.jellysquid.mods.lithium.common.block.entity.inventory_comparator_tracking.ComparatorTracker;
import me.jellysquid.mods.lithium.common.hopper.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.BooleanSupplier;

@Mixin(value = BetterExtractingHopperBlockEntity.class)
public abstract class BetterExtractingHopperBlockEntityMixin extends BetterHopperBlockEntityMixin<PullHopperBlockEntity> {
	@Unique
	@NotNull
	private HopperCachingState.BlockInventory extractionMode = HopperCachingState.BlockInventory.UNKNOWN;
	@Unique
	private long extractStackListModCount;
	@Unique
	private long myModCountAtLastExtract;

	@Unique
	private @Nullable Inventory extractBlockInventory;
	@Unique
	private @Nullable LithiumInventory extractInventory;
	@Unique
	private @Nullable LithiumStackList extractStackList;

	public BetterExtractingHopperBlockEntityMixin(BlockEntityType<PullHopperBlockEntity> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState, int size, int MAX_COOLDOWN) {
		super(blockEntityType, blockPos, blockState, size, MAX_COOLDOWN);
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@WrapMethod(method = "extract", remap = false)
	public boolean extractHook(Operation<Boolean> fallback) {
		var extractInventory = getExtractBlockInventory(world);
		return lithiumExtract(extractInventory, fallback::call);
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "scheduledTick",
	        at = @At("RETURN"))
	public void scheduledTickHook(World world, BlockPos pos, BlockState blockState, CallbackInfo callbackInfo) {
		this.checkSleepingConditions();
	}


	public void lithium$invalidateCacheOnNeighborUpdate(boolean fromAbove) {
		if (fromAbove) {
			if (this.extractionMode == HopperCachingState.BlockInventory.NO_BLOCK_INVENTORY || this.extractionMode == HopperCachingState.BlockInventory.BLOCK_STATE) {
				this.invalidateBlockExtractionData();
			}
		} else if (this.insertionMode == HopperCachingState.BlockInventory.NO_BLOCK_INVENTORY || this.insertionMode == HopperCachingState.BlockInventory.BLOCK_STATE) {
			this.invalidateBlockInsertionData();
		}

	}

	public void lithium$invalidateCacheOnNeighborUpdate(Direction fromDirection) {
		boolean fromAbove = fromDirection == Direction.UP;
		if (fromAbove || this.getCachedState().get(HopperBlock.FACING) == fromDirection) {
			this.lithium$invalidateCacheOnNeighborUpdate(fromAbove);
		}

	}

	@Unique
	private Inventory getExtractBlockInventory(World world) {
		Inventory blockInventory = this.extractBlockInventory;
		switch (this.extractionMode) {
			case NO_BLOCK_INVENTORY -> {
				return null;
			}
			case BLOCK_STATE, REMOVAL_TRACKING_BLOCK_ENTITY -> {
				return blockInventory;
			}
			default -> {
				BlockPos pos;
				if (this.extractionMode == HopperCachingState.BlockInventory.BLOCK_ENTITY) {
					BlockEntity blockEntity = (BlockEntity) Objects.requireNonNull(blockInventory);
					pos = blockEntity.getPos();
					BlockPos transferPos = this.getPos().up();
					if (!blockEntity.isRemoved() && pos.equals(transferPos)) {
						LithiumInventory optimizedInventory;
						if ((optimizedInventory = this.extractInventory) == null) {
							return blockInventory;
						}

						LithiumStackList insertInventoryStackList = InventoryHelper.getLithiumStackList(
								optimizedInventory);
						if (insertInventoryStackList == this.extractStackList) {
							return optimizedInventory;
						}

						this.invalidateBlockExtractionData();
					}
				}
				pos = this.getPos().up();
				BlockState blockState = world.getBlockState(pos);
				blockInventory = HopperBlockEntityInvoker.invokeGetBlockInventoryAt(world, pos, blockState);
				blockInventory = HopperHelper.replaceDoubleInventory(blockInventory);
				this.cacheExtractBlockInventory(blockInventory);
				return blockInventory;
			}
		}
	}

	@Unique
	private void cacheExtractBlockInventory(Inventory extractInventory) {
		assert !(extractInventory instanceof Entity);

		if (extractInventory instanceof LithiumInventory optimizedInventory) {
			this.cacheExtractLithiumInventory(optimizedInventory);
		} else {
			this.extractInventory = null;
			this.extractStackList = null;
			this.extractStackListModCount = 0L;
		}

		if (!(extractInventory instanceof BlockEntity) && !(extractInventory instanceof DoubleInventory)) {
			if (extractInventory == null) {
				this.extractBlockInventory = null;
				this.extractionMode = HopperCachingState.BlockInventory.NO_BLOCK_INVENTORY;
			} else {
				this.extractBlockInventory = extractInventory;
				this.extractionMode = extractInventory instanceof BlockStateOnlyInventory
				                      ? HopperCachingState.BlockInventory.BLOCK_STATE
				                      : HopperCachingState.BlockInventory.UNKNOWN;
			}
		} else {
			this.extractBlockInventory = extractInventory;
			if (extractInventory instanceof InventoryChangeTracker) {
				this.extractionMode = HopperCachingState.BlockInventory.REMOVAL_TRACKING_BLOCK_ENTITY;
				((InventoryChangeTracker) extractInventory).listenForMajorInventoryChanges(this);
			} else {
				this.extractionMode = HopperCachingState.BlockInventory.BLOCK_ENTITY;
			}
		}

	}

	public void lithium$handleInventoryRemoved(Inventory inventory) {
		this.wakeUpNow();

		if (inventory == this.insertBlockInventory) {
			this.invalidateBlockInsertionData();
		}

		if (inventory == this.extractBlockInventory) {
			this.invalidateBlockExtractionData();
		}

		if (inventory == this) {
			this.invalidateCachedData();
		}
	}

	@Unique
	private void invalidateCachedData() {
		this.shouldCheckSleep = false;
		this.invalidateInsertionData();
		this.invalidateExtractionData();
	}

	@Unique
	private void invalidateExtractionData() {
		if (this.extractionMode == HopperCachingState.BlockInventory.REMOVAL_TRACKING_BLOCK_ENTITY) {
			assert this.extractBlockInventory != null;

			((InventoryChangeTracker) this.extractBlockInventory).stopListenForMajorInventoryChanges(this);
		}

		this.invalidateBlockExtractionData();
	}

	@Unique
	private void invalidateBlockExtractionData() {
		this.extractionMode = HopperCachingState.BlockInventory.UNKNOWN;
		this.extractBlockInventory = null;
		this.extractInventory = null;
		this.extractStackList = null;
		this.extractStackListModCount = 0L;
		this.wakeUpNow();
	}

	@Unique
	private void cacheExtractLithiumInventory(LithiumInventory optimizedInventory) {
		LithiumStackList extractInventoryStackList = InventoryHelper.getLithiumStackList(optimizedInventory);
		this.extractInventory = optimizedInventory;
		this.extractStackList = extractInventoryStackList;
		this.extractStackListModCount = extractInventoryStackList.getModCount() - 1L;
	}

	@Unique
	private void checkSleepingConditions() {
		if (!(this.cooldown > 0)) {
			if (this.isSleeping()) {
				return;
			}

			if (!this.shouldCheckSleep) {
				this.shouldCheckSleep = true;
				return;
			}

			boolean listenToExtractTracker = false;
			boolean listenToInsertTracker = false;
			LithiumStackList thisStackList = InventoryHelper.getLithiumStackList(this);
			Inventory blockInventory;
			if (this.extractionMode != HopperCachingState.BlockInventory.BLOCK_STATE && thisStackList.getFullSlots() != thisStackList.size()) {
				if (this.extractionMode == HopperCachingState.BlockInventory.REMOVAL_TRACKING_BLOCK_ENTITY) {
					blockInventory = this.extractBlockInventory;
					if (this.extractStackList != null && blockInventory instanceof InventoryChangeTracker) {
						if (!this.extractStackList.maybeSendsComparatorUpdatesOnFailedExtract() || (blockInventory instanceof ComparatorTracker comparatorTracker && !comparatorTracker.lithium$hasAnyComparatorNearby())) {
							listenToExtractTracker = true;
						} else {
							return;
						}
					} else {
						return;
					}
				} else {
					if (this.extractionMode != HopperCachingState.BlockInventory.NO_BLOCK_INVENTORY) {
						return;
					}
				}
			}

			if (this.insertionMode != HopperCachingState.BlockInventory.BLOCK_STATE && 0 < thisStackList.getOccupiedSlots()) {
				if (this.insertionMode == HopperCachingState.BlockInventory.REMOVAL_TRACKING_BLOCK_ENTITY) {
					blockInventory = this.insertBlockInventory;
					if (this.insertStackList == null || !(blockInventory instanceof InventoryChangeTracker)) {
						return;
					}

					listenToInsertTracker = true;
				} else {
					if (this.insertionMode != HopperCachingState.BlockInventory.NO_BLOCK_INVENTORY) {
						return;
					}
				}
			}

			if (listenToExtractTracker) {
				((InventoryChangeTracker) this.extractBlockInventory).listenForContentChangesOnce(
						this.extractStackList,
						this
				);
			}

			if (listenToInsertTracker) {
				((InventoryChangeTracker) this.insertBlockInventory).listenForContentChangesOnce(
						this.insertStackList,
						this
				);
			}

			this.listenForContentChangesOnce(thisStackList, this);
			this.lithium$startSleeping();
		}
	}

	public boolean lithium$handleComparatorAdded(Inventory inventory) {
		if (inventory == this.extractBlockInventory) {
			this.wakeUpNow();
			return true;
		} else {
			return false;
		}
	}

	@Unique
	private boolean lithiumExtract(Inventory from, BooleanSupplier fallback) {
		if (from != this.extractInventory || this.extractStackList == null) {
			return fallback.getAsBoolean(); //from inventory is not an optimized inventory, vanilla fallback
		}

		LithiumStackList hopperStackList = InventoryHelper.getLithiumStackList(this);
		LithiumStackList fromStackList = this.extractStackList;

		if (hopperStackList.getModCount() == this.myModCountAtLastExtract) {
			if (fromStackList.getModCount() == this.extractStackListModCount) {
				if (!(from instanceof ComparatorTracker comparatorTracker) || comparatorTracker.lithium$hasAnyComparatorNearby()) {
					//noinspection CollectionAddedToSelf
					fromStackList.runComparatorUpdatePatternOnFailedExtract(fromStackList, from);
				}
				return false;
			}
		}

		int[] availableSlots = from instanceof SidedInventory sidedInventory
		                       ? sidedInventory.getAvailableSlots(Direction.DOWN)
		                       : null;
		int fromSize = availableSlots != null
		               ? availableSlots.length
		               : from.size();
		for (int i = 0; i < fromSize; i++) {
			int fromSlot = availableSlots != null
			               ? availableSlots[i]
			               : i;
			ItemStack itemStack = fromStackList.get(fromSlot);
			if (!itemStack.isEmpty() && HopperBlockEntityInvoker.invokeCanExtract(
					this,
					from,
					itemStack,
					fromSlot,
					Direction.DOWN
			)) {
				//calling removeStack is necessary due to its side effects (markDirty in LootableContainerBlockEntity)
				ItemStack takenItem = from.removeStack(fromSlot, 1);
				assert !takenItem.isEmpty();
				boolean transferSuccess = HopperHelper.tryMoveSingleItem(this, takenItem, null);
				if (transferSuccess) {
					this.markDirty();
					from.markDirty();
					return true;
				}
				//put the item back similar to vanilla
				ItemStack restoredStack = fromStackList.get(fromSlot);
				if (restoredStack.isEmpty()) {
					restoredStack = takenItem;
				} else {
					restoredStack.increment(1);
				}
				//calling setStack is necessary due to its side effects (markDirty in LootableContainerBlockEntity)
				from.setStack(fromSlot, restoredStack);
			}
		}
		this.myModCountAtLastExtract = hopperStackList.getModCount();
		if (fromStackList != null) {
			this.extractStackListModCount = fromStackList.getModCount();
		}
		return false;
	}
}
