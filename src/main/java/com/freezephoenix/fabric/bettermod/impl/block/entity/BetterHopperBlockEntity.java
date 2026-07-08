package com.freezephoenix.fabric.bettermod.impl.block.entity;

import com.freezephoenix.fabric.bettermod.api.block.entity.TickOnInterval;
import com.freezephoenix.fabric.bettermod.impl.util.TransferType;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public abstract class BetterHopperBlockEntity<T extends BetterHopperBlockEntity<T>> extends TickOnInterval<T> {
	public static final int MAX_COOLDOWN = 8;
	protected BlockPos.MutableBlockPos insertionPos = new BlockPos.MutableBlockPos();
	protected Direction facing;
	protected @Nullable BlockApiCache<Storage<ItemVariant>, Direction> PUSH_TARGET_CACHE;

	public BetterHopperBlockEntity(BlockEntityType<T> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState, 5);
		facing = blockState.getValue(BlockStateProperties.FACING_HOPPER);
		insertionPos.setWithOffset(getBlockPos(),facing);
	}

	protected abstract TransferType getTransferType();

	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
		return new HopperMenu(syncId, playerInventory, this);
	}

	protected @Nullable Storage<ItemVariant> getPushTarget(ServerLevel world) {
		if(PUSH_TARGET_CACHE == null || !insertionPos.equals(PUSH_TARGET_CACHE.getPos())) {
			PUSH_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, world, insertionPos);
		}
		return PUSH_TARGET_CACHE.find(facing.getOpposite());
	}

	protected @Nullable Storage<ItemVariant> getPullTarget(ServerLevel world) {
		return null;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setBlockState(BlockState state) {
		super.setBlockState(state);
		facing = state.getValue(BlockStateProperties.FACING_HOPPER);
		insertionPos.setWithOffset(getBlockPos(), facing);
	}

	protected final boolean transfer(ServerLevel world) {
		final var PUSH_TARGET = getPushTarget(world);
		final var PULL_TARGET = getPullTarget(world);
		TransferType.TransferResult result = getTransferType().handle(PULL_TARGET, SELF, PUSH_TARGET);
		if (result.inserted()) {
			assert PUSH_TARGET != null;
			boolean PUSH_TARGET_EMPTY = !PUSH_TARGET.nonEmptyIterator().hasNext();
			if (PUSH_TARGET_EMPTY) {
				assert PUSH_TARGET_CACHE != null;
				if (PUSH_TARGET_CACHE.getBlockEntity() instanceof BetterHopperBlockEntity<?> destinationHopperBlockEntity) {
					if (destinationHopperBlockEntity.LAST_TICK >= this.LAST_TICK) {
						destinationHopperBlockEntity.setCooldown(MAX_COOLDOWN - 1);
					} else {
						destinationHopperBlockEntity.setCooldown(MAX_COOLDOWN);
					}
				}
			}
		}
		return result.inserted() | result.extracted();
	}

	@Override
	public void onCooldown(ServerLevel world, BlockPos pos, BlockState blockState) {
		if (this.transfer(world)) {
			setCooldown(BetterHopperBlockEntity.MAX_COOLDOWN);
		}
	}
}
