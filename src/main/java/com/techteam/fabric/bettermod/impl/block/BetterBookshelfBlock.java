package com.techteam.fabric.bettermod.impl.block;

import com.mojang.serialization.MapCodec;
import com.techteam.fabric.bettermod.api.block.BetterBlock;
import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.block.entity.BetterBookshelfBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class BetterBookshelfBlock extends BetterBlock<BetterBookshelfBlockEntity> {
	public static final Identifier ID = Identifier.of("minecraft", "bookshelf");
	public static final MapCodec<BetterBookshelfBlock> CODEC = BetterBookshelfBlock.createCodec(BetterBookshelfBlock::new);

	public BetterBookshelfBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<BetterBookshelfBlock> getCodec() {
		return CODEC;
	}
}
