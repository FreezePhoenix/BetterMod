package com.freezephoenix.fabric.bettermod.impl.block;

import com.freezephoenix.fabric.bettermod.api.block.InventoryBetterBlock;
import com.freezephoenix.fabric.bettermod.impl.BetterMod;
import com.mojang.serialization.MapCodec;
import com.freezephoenix.fabric.bettermod.impl.block.entity.BetterBookshelfBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BetterBookshelfBlock extends InventoryBetterBlock<BetterBookshelfBlockEntity> {
	public static final MapCodec<BetterBookshelfBlock> CODEC = BetterBookshelfBlock.simpleCodec(BetterBookshelfBlock::new);

	public BetterBookshelfBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected MapCodec<BetterBookshelfBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockEntityType<BetterBookshelfBlockEntity> getBlockEntityType() { return BetterMod.BOOKSHELF.entity(); }
}
