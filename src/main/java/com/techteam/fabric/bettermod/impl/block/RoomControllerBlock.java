package com.techteam.fabric.bettermod.impl.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public final class RoomControllerBlock extends Block {
	public static final Identifier ID = Identifier.of("bettermod", "room_controller");
	public static final MapCodec<RoomControllerBlock> CODEC = RoomControllerBlock.createCodec(RoomControllerBlock::new);

	public RoomControllerBlock(Settings settings) {
		super(settings.dynamicBounds().hardness(4.0f).nonOpaque());
	}

	@Override
	protected MapCodec<RoomControllerBlock> getCodec() {
		return CODEC;
	}
}
