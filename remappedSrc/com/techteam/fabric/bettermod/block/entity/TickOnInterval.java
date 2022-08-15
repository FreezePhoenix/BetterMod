package com.techteam.fabric.bettermod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class TickOnInterval extends BetterBlockEntity implements ITickable {
	private final int MAX_COOLDOWN;
	private int cooldown = 0;

	public TickOnInterval(BlockEntityType<?> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState, int MAX_COOLDOWN) {
		super(blockEntityType, blockPos, blockState);
		this.MAX_COOLDOWN = MAX_COOLDOWN;
	}

	public TickOnInterval(BlockEntityType<?> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState, int size, int MAX_COOLDOWN) {
		super(blockEntityType, blockPos, blockState, size);
		this.MAX_COOLDOWN = MAX_COOLDOWN;
	}

	public abstract void update(World world, BlockPos pos, BlockState blockState);

	@Override
	public void tick(World world, BlockPos pos, BlockState blockState) {
		if (!world.isClient()) {
			if (cooldown > 0) {
				cooldown--;
				return;
			}
			update(world, pos, blockState);
			cooldown = MAX_COOLDOWN;
		}
	}
}
