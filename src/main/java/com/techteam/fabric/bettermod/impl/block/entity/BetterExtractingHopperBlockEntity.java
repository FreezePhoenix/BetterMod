package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.impl.util.TransferType;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class BetterExtractingHopperBlockEntity<T extends BetterExtractingHopperBlockEntity<T>> extends BetterHopperBlockEntity<T> {
	protected BlockApiCache<Storage<ItemVariant>, Direction> PULL_TARGET_CACHE;

	public BetterExtractingHopperBlockEntity(BlockEntityType<T> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	protected final boolean extract(ServerWorld world) {
		if(PULL_TARGET_CACHE == null) {
			PULL_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, world, pos.up());
		}
		Storage<ItemVariant> PULL_TARGET = PULL_TARGET_CACHE.find(Direction.DOWN);
		if (PULL_TARGET != null) {
			return getExtractionTransferType().handle(PULL_TARGET, SELF);
		}
		return false;
	}

	protected abstract TransferType getExtractionTransferType();

	@Override
	public void onCooldown(ServerWorld world, BlockPos pos, BlockState blockState) {
		if (this.insert(world) | this.extract(world)) {
			setCooldown(BetterHopperBlockEntity.MAX_COOLDOWN);
		}
	}
}
