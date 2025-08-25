package com.techteam.fabric.bettermod.api.block;

import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.api.block.entity.ITickable;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class BetterTickingBlock<E extends BetterBlockEntity & ITickable> extends BetterBlock<E> {
	public BetterTickingBlock(@NotNull Settings settings) {
		super(settings);
	}

	static <E extends BetterBlockEntity & ITickable> void tick(World world, BlockPos pos, BlockState state, E blockEntity) {
		blockEntity.tick(world, pos, state);
	}

	@Override
	public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient() ? null : validateTicker(type, blockEntityType, BetterTickingBlock::tick);
	}
}
