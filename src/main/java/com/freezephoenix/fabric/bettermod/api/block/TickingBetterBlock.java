package com.freezephoenix.fabric.bettermod.api.block;

import com.freezephoenix.fabric.bettermod.api.block.entity.TickingBetterBlockEntity;
import com.freezephoenix.fabric.bettermod.impl.mixin.BaseEntityBlockAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;


public interface TickingBetterBlock<E extends BlockEntity & TickingBetterBlockEntity> extends BetterBlock<E>, EntityBlock {
	@Override
	default <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> actual) {
		return world.isClientSide() ? null : BaseEntityBlockAccessor.createTickerHelper(actual, getBlockEntityType(), TickingBetterBlockEntity::tick);
	}
}
