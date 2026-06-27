package com.freezephoenix.fabric.bettermod.impl.block.entity;

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

	@Override
	protected Storage<ItemVariant> getPullTarget(ServerLevel world) {
		if(PULL_TARGET_CACHE == null) {
			PULL_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, world, worldPosition.above());
		}
		return PULL_TARGET_CACHE.find(Direction.DOWN);
	}
}
