package com.techteam.fabric.bettermod.api.block;

import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class BetterBlock<E extends BetterBlockEntity> extends BlockWithEntity {

	public BlockEntityType<E> blockEntityType;

	public BetterBlock(@NotNull Settings settings) {
		super(settings);
	}

	@Override
	public final E createBlockEntity(BlockPos pos, BlockState state) {
		return blockEntityType.instantiate(pos, state);
	}

	@Override
	public @NotNull BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
		ItemScatterer.onStateReplaced(state, world, pos);
		super.onStateReplaced(state, world, pos, moved);
	}

	@Override
	public @NotNull ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, @NotNull PlayerEntity player, BlockHitResult hit) {
		if (!world.isClient()) {
			if(world.getBlockEntity(pos) instanceof BetterBlockEntity betterBlockEntity) {
				player.openHandledScreen(betterBlockEntity);
			}
		}

		return ActionResult.CONSUME;
	}
}
