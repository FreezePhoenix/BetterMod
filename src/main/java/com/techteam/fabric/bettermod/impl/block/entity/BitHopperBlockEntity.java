package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IServerLoadableBlockEntity;
import com.techteam.fabric.bettermod.impl.util.Texts;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BitHopperBlockEntity extends BetterHopperBlockEntity<BitHopperBlockEntity> implements IServerLoadableBlockEntity {
	public static final Identifier ID = Identifier.of("bettermod", "bit_hopper");

	public BitHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.BIT_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public Text getContainerName() {
		return Texts.BIT_HOPPER;
	}
}
