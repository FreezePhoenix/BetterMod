package com.techteam.fabric.bettermod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class TickOnInterval extends BetterBlockEntity implements ITickable {
	private static final String DELAY_ID = "TickOnInterval::cooldown";
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
	public void writeNbt(@NotNull NbtCompound tag) {
		tag.putInt(DELAY_ID, cooldown);
		super.writeNbt(tag);
	}

	@Override
	public void readNbt(@NotNull NbtCompound tag) {
		if(tag.getType(DELAY_ID) == NbtElement.INT_TYPE) {
			this.cooldown = tag.getInt(DELAY_ID);
		}
		super.readNbt(tag);
	}

	@Override
	public final void tick(World world, BlockPos pos, BlockState blockState) {
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
