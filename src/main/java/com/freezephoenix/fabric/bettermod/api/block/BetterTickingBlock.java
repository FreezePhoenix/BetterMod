package com.freezephoenix.fabric.bettermod.api.block;

import com.freezephoenix.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.freezephoenix.fabric.bettermod.api.block.entity.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public abstract class BetterTickingBlock<E extends BetterBlockEntity<E> & ITickable> extends BetterBlock<E> {
	public BetterTickingBlock(Properties settings) {
		super(settings);
	}

	static <E extends BetterBlockEntity<E> & ITickable> void tick(Level world, BlockPos pos, BlockState state, E blockEntity) {
		blockEntity.tick((ServerLevel) world, pos, state);
	}

	@Override
	public final <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return world.isClientSide()
			   ? null
			   : createTickerHelper(type, blockEntityType, BetterTickingBlock::tick);
	}
}
