package com.techteam.fabric.bettermod.block;

import com.techteam.fabric.bettermod.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.block.entity.ITickable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BetterTickingBlock<E extends BetterBlockEntity & ITickable> extends BetterBlock<E> {
	public BetterTickingBlock(@NotNull Settings settings) {
		super(settings);
	}
	@NotNull
	@Override
	public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return BetterTickingBlock::tick;
	}
	static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockEntity) {
		if(blockEntity instanceof ITickable tickable) {
			tickable.tick(world, pos, state);
		}
	}
}
