package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.impl.util.TransferType;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class BetterExtractingHopperBlockEntity<T extends BetterExtractingHopperBlockEntity<T>> extends BetterHopperBlockEntity<T> {
	protected BlockApiCache<Storage<ItemVariant>, Direction> PULL_TARGET_CACHE;

	public BetterExtractingHopperBlockEntity(BlockEntityType<T> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	protected final boolean extract(ServerLevel world) {
		if(PULL_TARGET_CACHE == null) {
			PULL_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, world, worldPosition.above());
		}
		Storage<ItemVariant> PULL_TARGET = PULL_TARGET_CACHE.find(Direction.DOWN);
		if (PULL_TARGET != null) {
			return getExtractionTransferType().handle(PULL_TARGET, SELF);
		}
		return false;
	}

	protected abstract TransferType getExtractionTransferType();

	@Override
	public void onCooldown(ServerLevel world, BlockPos pos, BlockState blockState) {
		if (this.insert(world) | this.extract(world)) {
			setCooldown(BetterHopperBlockEntity.MAX_COOLDOWN);
		}
	}
}
