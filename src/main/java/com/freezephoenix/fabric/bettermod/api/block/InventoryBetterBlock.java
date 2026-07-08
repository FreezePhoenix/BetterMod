package com.freezephoenix.fabric.bettermod.api.block;

import com.freezephoenix.fabric.bettermod.api.block.entity.InventoryBetterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class InventoryBetterBlock<E extends InventoryBetterBlockEntity<E>> extends BaseEntityBlock implements BetterBlock<E> {
	public InventoryBetterBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
		Containers.updateNeighboursAfterDestroy(state, world, pos);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (!world.isClientSide()) {
			if (getBlockEntityType().getBlockEntity(world, pos) instanceof InventoryBetterBlockEntity<E> betterBlockEntity) {
				player.openMenu(betterBlockEntity);
			}
		}

		return InteractionResult.SUCCESS;
	}
}
