package com.freezephoenix.fabric.bettermod.block;

import com.freezephoenix.fabric.api.block.InventoryBetterBlock;
import com.freezephoenix.fabric.bettermod.BetterMod;
import com.mojang.serialization.MapCodec;
import com.freezephoenix.fabric.bettermod.block.entity.BetterBookshelfBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BetterBookshelfBlock extends InventoryBetterBlock<BetterBookshelfBlockEntity> {

	public BetterBookshelfBlock(Properties settings) {
		super(settings);
	}

	@Override
	public BlockEntityType<BetterBookshelfBlockEntity> getBlockEntityType() { return BetterMod.BOOKSHELF.entity(); }
}
