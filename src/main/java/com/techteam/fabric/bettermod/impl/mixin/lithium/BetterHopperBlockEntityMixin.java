package com.techteam.fabric.bettermod.impl.mixin.lithium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.techteam.fabric.bettermod.api.block.entity.TickOnInterval;
import com.techteam.fabric.bettermod.impl.block.entity.BetterHopperBlockEntity;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import me.jellysquid.mods.lithium.api.inventory.LithiumCooldownReceivingInventory;
import me.jellysquid.mods.lithium.api.inventory.LithiumInventory;
import me.jellysquid.mods.lithium.common.block.entity.SleepingBlockEntity;
import me.jellysquid.mods.lithium.common.block.entity.inventory_change_tracking.InventoryChangeListener;
import me.jellysquid.mods.lithium.common.block.entity.inventory_change_tracking.InventoryChangeTracker;
import me.jellysquid.mods.lithium.common.hopper.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.BooleanSupplier;

@Mixin(value = BetterHopperBlockEntity.class,
       remap = false)
public abstract class BetterHopperBlockEntityMixin<T extends BetterHopperBlockEntity<T>> extends TickOnInterval<T> implements LithiumInventory, SleepingBlockEntity, UpdateReceiver, InventoryChangeListener, InventoryChangeTracker {
	@Unique
	protected long insertStackListModCount;
	@Unique
	@NotNull
	protected HopperCachingState.BlockInventory insertionMode = HopperCachingState.BlockInventory.UNKNOWN;
	@Unique
	@Nullable
	protected Inventory insertBlockInventory;
	@Unique
	@Nullable
	protected LithiumInventory insertInventory;
	@Unique
	@Nullable
	protected LithiumStackList insertStackList;
	@Unique
	protected long myModCountAtLastInsert;
	@Unique
	protected boolean shouldCheckSleep;
	@Shadow
	protected BlockPos insertionPos;
	@Shadow
	protected Direction facing;
	@Unique
	ReferenceArraySet<InventoryChangeListener> inventoryChangeListeners = null;
	@Unique
	ReferenceArraySet<InventoryChangeListener> inventoryHandlingTypeListeners = null;


	public BetterHopperBlockEntityMixin(BlockEntityType<T> blockEntityType,
	                                    @NotNull BlockPos blockPos, BlockState blockState, int size, int MAX_COOLDOWN) {
		super(blockEntityType, blockPos, blockState, size, MAX_COOLDOWN);
	}

	@WrapMethod(method = "insert")
	public boolean insertHook(Operation<Boolean> fallback) {
		return lithiumInsert(getInsertBlockInventory(world), fallback::call);
	}


	@Inject(method = "scheduledTick",
	        at = @At("TAIL"))
	public void scheduledTickHook(World world, BlockPos pos, BlockState blockState, CallbackInfo callbackInfo) {
		this.checkSleepingConditions();
	}

	@Unique
	protected boolean lithiumInsert(Inventory insertInventory, BooleanSupplier fallback) {
		if (insertInventory == null) {
			return fallback.getAsBoolean();
		}

		LithiumStackList hopperStackList = InventoryHelper.getLithiumStackList(this);
		if (this.insertInventory != insertInventory || hopperStackList.getModCount() != this.myModCountAtLastInsert || this.insertStackList == null || this.insertStackList.getModCount() != this.insertStackListModCount) {
			boolean insertInventoryWasEmptyHopperNotDisabled = insertInventory instanceof BetterHopperBlockEntity<?> && this.insertStackList != null && this.insertStackList.getOccupiedSlots() == 0;
			boolean insertInventoryHandlesModdedCooldown = ((LithiumCooldownReceivingInventory) insertInventory).canReceiveTransferCooldown() && this.insertStackList != null
			                                               ? this.insertStackList.getOccupiedSlots() == 0
			                                               : insertInventory.isEmpty();
			if (!(this.insertInventory == insertInventory && this.insertStackList.getFullSlots() == this.insertStackList.size())) {
				Direction fromDirection = this.facing.getOpposite();
				int size = hopperStackList.size();
				//noinspection ForLoopReplaceableByForEach
				for (int i = 0; i < size; ++i) {
					ItemStack transferStack = hopperStackList.get(i);
					if (!transferStack.isEmpty() && HopperBlockEntityInvoker.invokeCanExtract(
							insertInventory,
							this,
							transferStack,
							size,
							facing
					)) {
						boolean transferSuccess = HopperHelper.tryMoveSingleItem(
								insertInventory,
								transferStack,
								fromDirection
						);
						if (transferSuccess) {
							if (insertInventoryWasEmptyHopperNotDisabled) {
								BetterHopperBlockEntity<?> receivingHopper = (BetterHopperBlockEntity<?>) insertInventory;
								int k = 8;
								if (receivingHopper.LAST_TICK >= this.LAST_TICK) {
									k = 7;
								}
								receivingHopper.setCooldown(k);
							}
							if (insertInventoryHandlesModdedCooldown) {
								((LithiumCooldownReceivingInventory) insertInventory).setTransferCooldown(this.LAST_TICK);
							}
							insertInventory.markDirty();
							return true;
						}
					}
				}
			}

			this.myModCountAtLastInsert = hopperStackList.getModCount();
			if (this.insertStackList != null) {
				this.insertStackListModCount = this.insertStackList.getModCount();
			}
		}
		return false;
	}

	@Unique
	protected Inventory getInsertBlockInventory(World world) {
		Inventory blockInventory = this.insertBlockInventory;
		switch (this.insertionMode) {
			case NO_BLOCK_INVENTORY -> {
				return null;
			}
			case BLOCK_STATE, REMOVAL_TRACKING_BLOCK_ENTITY -> {
				return blockInventory;
			}
			default -> {
				BlockPos pos;
				if (this.insertionMode == HopperCachingState.BlockInventory.BLOCK_ENTITY) {
					BlockEntity blockEntity = (BlockEntity) Objects.requireNonNull(blockInventory);
					pos = blockEntity.getPos();
					if (!blockEntity.isRemoved() && pos.equals(insertionPos)) {
						LithiumInventory optimizedInventory;
						if ((optimizedInventory = this.insertInventory) == null) {
							return blockInventory;
						}

						LithiumStackList insertInventoryStackList = InventoryHelper.getLithiumStackList(
								optimizedInventory);
						if (insertInventoryStackList == this.insertStackList) {
							return optimizedInventory;
						}

						this.invalidateBlockInsertionData();
					}
				}

				BlockState blockState = world.getBlockState(insertionPos);
				blockInventory = HopperBlockEntityInvoker.invokeGetBlockInventoryAt(world, insertionPos, blockState);
				blockInventory = HopperHelper.replaceDoubleInventory(blockInventory);
				this.cacheInsertBlockInventory(blockInventory);
				return blockInventory;
			}
		}
	}

	@Unique
	protected void invalidateInsertionData() {
		if (this.insertionMode == HopperCachingState.BlockInventory.REMOVAL_TRACKING_BLOCK_ENTITY) {
			assert this.insertBlockInventory != null;

			((InventoryChangeTracker) this.insertBlockInventory).stopListenForMajorInventoryChanges(this);
		}

		this.invalidateBlockInsertionData();
	}

	@Unique
	protected void invalidateBlockInsertionData() {
		this.insertionMode = HopperCachingState.BlockInventory.UNKNOWN;
		this.insertBlockInventory = null;
		this.insertInventory = null;
		this.insertStackList = null;
		this.insertStackListModCount = 0L;
		this.wakeUpNow();

	}

	public void lithium$handleInventoryContentModified(Inventory inventory) {
		this.wakeUpNow();
	}

	@Unique
	private void cacheInsertBlockInventory(Inventory insertInventory) {
		assert !(insertInventory instanceof Entity);

		if (insertInventory instanceof LithiumInventory optimizedInventory) {
			this.cacheInsertLithiumInventory(optimizedInventory);
		} else {
			this.insertInventory = null;
			this.insertStackList = null;
			this.insertStackListModCount = 0L;
		}

		if (!(insertInventory instanceof BlockEntity) && !(insertInventory instanceof DoubleInventory)) {
			if (insertInventory == null) {
				this.insertBlockInventory = null;
				this.insertionMode = HopperCachingState.BlockInventory.NO_BLOCK_INVENTORY;
			} else {
				this.insertBlockInventory = insertInventory;
				this.insertionMode = insertInventory instanceof BlockStateOnlyInventory
				                     ? HopperCachingState.BlockInventory.BLOCK_STATE
				                     : HopperCachingState.BlockInventory.UNKNOWN;
			}
		} else {
			this.insertBlockInventory = insertInventory;
			if (insertInventory instanceof InventoryChangeTracker) {
				this.insertionMode = HopperCachingState.BlockInventory.REMOVAL_TRACKING_BLOCK_ENTITY;
				((InventoryChangeTracker) insertInventory).listenForMajorInventoryChanges(this);
			} else {
				this.insertionMode = HopperCachingState.BlockInventory.BLOCK_ENTITY;
			}
		}
	}

	@Unique
	private void cacheInsertLithiumInventory(LithiumInventory optimizedInventory) {
		LithiumStackList insertInventoryStackList = InventoryHelper.getLithiumStackList(optimizedInventory);
		this.insertInventory = optimizedInventory;
		this.insertStackList = insertInventoryStackList;
		this.insertStackListModCount = insertInventoryStackList.getModCount() - 1L;
	}

	public void lithium$forwardMajorInventoryChanges(InventoryChangeListener inventoryChangeListener) {
		if (this.inventoryHandlingTypeListeners == null) {
			this.inventoryHandlingTypeListeners = new ReferenceArraySet<>(1);
		}

		this.inventoryHandlingTypeListeners.add(inventoryChangeListener);
	}

	public void lithium$stopForwardingMajorInventoryChanges(InventoryChangeListener inventoryChangeListener) {
		if (this.inventoryHandlingTypeListeners != null) {
			this.inventoryHandlingTypeListeners.remove(inventoryChangeListener);
		}
	}

	@Unique
	protected void invalidateChangeListening() {
		if (this.inventoryChangeListeners != null) {
			this.inventoryChangeListeners.clear();
		}

		LithiumStackList lithiumStackList = InventoryHelper.getLithiumStackListOrNull(this);
		if (lithiumStackList != null) {
			lithiumStackList.removeInventoryModificationCallback(this);
		}
	}

	public void lithium$emitFirstComparatorAdded() {
		ReferenceArraySet<InventoryChangeListener> inventoryChangeListeners = this.inventoryChangeListeners;
		if (inventoryChangeListeners != null && !inventoryChangeListeners.isEmpty()) {
			inventoryChangeListeners.removeIf((inventoryChangeListener) -> inventoryChangeListener.lithium$handleComparatorAdded(
					this));
		}
	}

	public void lithium$forwardContentChangeOnce(InventoryChangeListener inventoryChangeListener, LithiumStackList stackList, InventoryChangeTracker thisTracker) {
		if (this.inventoryChangeListeners == null) {
			this.inventoryChangeListeners = new ReferenceArraySet<>(1);
		}

		stackList.setInventoryModificationCallback(thisTracker);
		this.inventoryChangeListeners.add(inventoryChangeListener);
	}

	public void lithium$emitContentModified() {
		ReferenceArraySet<InventoryChangeListener> inventoryChangeListeners = this.inventoryChangeListeners;
		if (inventoryChangeListeners != null) {

			for (InventoryChangeListener inventoryChangeListener : inventoryChangeListeners) {
				inventoryChangeListener.lithium$handleInventoryContentModified(this);
			}

			inventoryChangeListeners.clear();
		}
	}

	public void lithium$emitStackListReplaced() {
		ReferenceArraySet<InventoryChangeListener> listeners = this.inventoryHandlingTypeListeners;
		if (listeners != null && !listeners.isEmpty()) {

			for (InventoryChangeListener inventoryChangeListener : listeners) {
				inventoryChangeListener.handleStackListReplaced(this);
			}

			listeners.clear();
		}
		this.handleStackListReplaced(this);
		this.invalidateChangeListening();
	}


	public void lithium$emitRemoved() {
		ReferenceArraySet<InventoryChangeListener> listeners = this.inventoryHandlingTypeListeners;
		if (listeners != null && !listeners.isEmpty()) {

			for (InventoryChangeListener listener : listeners) {
				listener.lithium$handleInventoryRemoved(this);
			}

			listeners.clear();
		}
		this.handleStackListReplaced(this);
		this.invalidateChangeListening();
	}

	public void lithium$invalidateCacheOnNeighborUpdate(boolean fromAbove) {
		if (!fromAbove) {
			if (this.insertionMode == HopperCachingState.BlockInventory.NO_BLOCK_INVENTORY || this.insertionMode == HopperCachingState.BlockInventory.BLOCK_STATE) {
				this.invalidateBlockInsertionData();
			}
		}
	}

	public void lithium$invalidateCacheOnNeighborUpdate(Direction fromDirection) {
		if (this.getCachedState().get(HopperBlock.FACING) == fromDirection) {
			this.lithium$invalidateCacheOnNeighborUpdate(false);
		}

	}

	public void lithium$handleInventoryRemoved(Inventory inventory) {
		this.wakeUpNow();

		if (inventory == this.insertBlockInventory) {
			this.invalidateBlockInsertionData();
		}

		if (inventory == this) {
			this.invalidateCachedData();
		}
	}

	@Unique
	private void invalidateCachedData() {
		this.shouldCheckSleep = false;
		this.invalidateInsertionData();
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

			boolean listenToInsertTracker = false;
			LithiumStackList thisStackList = InventoryHelper.getLithiumStackList(this);
			Inventory blockInventory;

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
		return false;
	}
}
