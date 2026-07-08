package com.freezephoenix.fabric.bettermod.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public interface BetterBlock<E extends BlockEntity> extends EntityBlock {
	@Override
	default @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
		return getBlockEntityType().create(worldPosition, blockState);
	}
	
	BlockEntityType<E> getBlockEntityType();
}
