package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.api.block.entity.TickOnInterval;
import com.techteam.fabric.bettermod.impl.util.InventoryUtil;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class BetterHopperBlockEntity<T extends BetterHopperBlockEntity<T>> extends TickOnInterval<T> {
	public static final int MAX_COOLDOWN = 8;
	protected BlockPos insertionPos;
	protected Direction facing;
	protected BlockApiCache<Storage<ItemVariant>, Direction> PUSH_TARGET_CACHE;

	public BetterHopperBlockEntity(BlockEntityType<T> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState, 5);
		facing = blockState.get(Properties.HOPPER_FACING);
		insertionPos = pos.offset(facing);
	}

	@Override
	public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new HopperScreenHandler(syncId, playerInventory, this);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setCachedState(BlockState state) {
		facing = state.get(Properties.HOPPER_FACING);
		insertionPos = pos.offset(facing);
		super.setCachedState(state);
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		if (world instanceof ServerWorld serverWorld) {
			PUSH_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, serverWorld, insertionPos);
		}
	}

	public boolean insert() {
		if (!insertionPos.equals(PUSH_TARGET_CACHE.getPos())) {
			PUSH_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) world, insertionPos);
		}
		Storage<ItemVariant> PUSH_TARGET = PUSH_TARGET_CACHE.find(facing.getOpposite());
		if (PUSH_TARGET != null) {
			boolean PUSH_TARGET_EMPTY = !PUSH_TARGET.nonEmptyIterator().hasNext();
			boolean result = InventoryUtil.handleTransfer(SELF, PUSH_TARGET);
			if (result) {
				if (PUSH_TARGET_EMPTY && PUSH_TARGET_CACHE.getBlockEntity() instanceof BetterExtractingHopperBlockEntity<?> extractingHopperBlockEntity) {
					if (extractingHopperBlockEntity.LAST_TICK >= this.LAST_TICK) {
						extractingHopperBlockEntity.setCooldown(MAX_COOLDOWN - 1, true);
					} else {
						extractingHopperBlockEntity.setCooldown(MAX_COOLDOWN, false);
					}
				}
			}
			return result;
		}
		return false;
	}

	@Override
	public void scheduledTick(World world, BlockPos pos, BlockState blockState) {
		boolean activated = false;
		// Push
		if (!isEmpty()) {
			activated = this.insert();
		}
		if (activated) {
			setCooldown(BetterHopperBlockEntity.MAX_COOLDOWN, false);
		}
	}
}
