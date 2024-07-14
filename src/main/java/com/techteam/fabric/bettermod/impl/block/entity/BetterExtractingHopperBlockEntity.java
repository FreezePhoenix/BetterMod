package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.impl.util.InventoryUtil;
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

	public boolean extract() {
		Storage<ItemVariant> PULL_TARGET = PULL_TARGET_CACHE.find(Direction.DOWN);
		if(PULL_TARGET != null) {
			return InventoryUtil.handleTransferStackable(PULL_TARGET, SELF);
		}
		return false;
	}

	@Override
	public void onServerLoad(ServerWorld world, BlockPos pos, BlockState state) {
		super.onServerLoad(world, pos, state);
		PULL_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, world, pos.up());
	}

	public boolean isFull() {
		return false;
	}

	@Override
	public void scheduledTick(World world, BlockPos pos, BlockState blockState) {
		boolean activated = false;
		if(!isEmpty()) {
			activated = this.insert();
		}

		if(!isFull()){
			activated |= this.extract();
		}

		if(activated) {
			setCooldown(MAX_COOLDOWN);
		}
	}
}
