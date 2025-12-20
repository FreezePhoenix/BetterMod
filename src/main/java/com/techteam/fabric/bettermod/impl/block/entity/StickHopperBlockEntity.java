package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.util.InventoryUtil;
import com.techteam.fabric.bettermod.impl.util.Texts;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public class StickHopperBlockEntity extends BetterExtractingHopperBlockEntity<StickHopperBlockEntity> {
	public static final Identifier ID = Identifier.of("bettermod", "stick_hopper");

	public StickHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.STICK_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public boolean extract() {
		Storage<ItemVariant> PULL_TARGET = PULL_TARGET_CACHE.find(Direction.DOWN);
		if (PULL_TARGET != null) {
			return InventoryUtil.handleTransferStackable(PULL_TARGET, SELF);
		}
		return false;
	}

	@Override
	public boolean insert() {
		if (!insertionPos.equals(PUSH_TARGET_CACHE.getPos())) {
			PUSH_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) world, insertionPos);
		}
		Storage<ItemVariant> PUSH_TARGET = PUSH_TARGET_CACHE.find(facing.getOpposite());
		if (PUSH_TARGET != null) {
			boolean PUSH_TARGET_EMPTY = !PUSH_TARGET.nonEmptyIterator().hasNext();
			boolean result = InventoryUtil.handleTransferSticky(SELF, PUSH_TARGET);
			if (result) {
				if (PUSH_TARGET_EMPTY && PUSH_TARGET_CACHE.getBlockEntity() instanceof BetterExtractingHopperBlockEntity<?> destinationHopperBlockEntity) {
					if (destinationHopperBlockEntity.LAST_TICK >= this.LAST_TICK) {
						destinationHopperBlockEntity.setCooldown(MAX_COOLDOWN - 1, true);
					} else {
						destinationHopperBlockEntity.setCooldown(MAX_COOLDOWN, false);
					}
				}
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (stack.getMaxCount() <= 1) {
			return false;
		}
		return super.isValid(slot, stack);
	}

	@Override
	public Text getContainerName() {
		return Texts.STICK_HOPPER;
	}
}
