package com.techteam.fabric.bettermod.api.block;

import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class BetterBlock<E extends BetterBlockEntity> extends BlockWithEntity {

	public BetterBlock(@NotNull Settings settings) {
		super(settings);
	}

	@Override
	public abstract E createBlockEntity(BlockPos pos, BlockState state);

	@Override
	public @NotNull BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState onBreak(@NotNull World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (worldIn.getBlockEntity(pos) instanceof BetterBlockEntity betterBlockEntity) {
			betterBlockEntity.dropItems();
		}
		return super.onBreak(worldIn, pos, state, player);
	}

	@Override
	public @NotNull ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, @NotNull PlayerEntity player, BlockHitResult hit) {
		if (world.isClient()) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen((BetterBlockEntity) world.getBlockEntity(pos));
			return ActionResult.CONSUME;
		}
	}
}
