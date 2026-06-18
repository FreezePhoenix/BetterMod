package com.techteam.fabric.bettermod.api.block;

import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class BetterBlock<E extends BetterBlockEntity<E>> extends BaseEntityBlock {

	public BlockEntityType<E> blockEntityType;

	public BetterBlock(@NotNull Properties settings) {
		super(settings);
	}

	@Override
	public final E newBlockEntity(BlockPos pos, BlockState state) {
		return blockEntityType.create(pos, state);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
		Containers.updateNeighboursAfterDestroy(state, world, pos);
	}

	@Override
	public @NotNull InteractionResult useWithoutItem(BlockState state, @NotNull Level world, BlockPos pos, @NotNull Player player, BlockHitResult hit) {
		if (!world.isClientSide()) {
			if (blockEntityType.getBlockEntity(world, pos) instanceof BetterBlockEntity<E> betterBlockEntity) {
				player.openMenu(betterBlockEntity);
			}
		}

		return InteractionResult.SUCCESS;
	}
}
