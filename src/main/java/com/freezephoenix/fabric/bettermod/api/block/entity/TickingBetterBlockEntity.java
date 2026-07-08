package com.freezephoenix.fabric.bettermod.api.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface TickingBetterBlockEntity {
	static <E extends TickingBetterBlockEntity> void tick(Level world, BlockPos pos, BlockState state, E blockEntity) {
		blockEntity.tick((ServerLevel) world, pos, state);
	}
	void tick(ServerLevel world, BlockPos pos, BlockState blockState);
}
