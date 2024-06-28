package com.techteam.fabric.bettermod.api.block;

import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.api.block.entity.ITickable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class BetterTickingBlock<E extends BetterBlockEntity & ITickable> extends BetterBlock<E> {
	public BetterTickingBlock(@NotNull Settings settings) {
		super(settings);
	}
	@Contract(pure = true)
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
