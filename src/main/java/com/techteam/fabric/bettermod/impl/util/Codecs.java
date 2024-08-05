package com.techteam.fabric.bettermod.impl.util;

import com.techteam.fabric.bettermod.impl.network.BoxUpdatePayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class Codecs {
	public static final PacketCodec<ByteBuf, BlockState> BLOCK_STATE = PacketCodecs.entryOf(Block.STATE_IDS);
	public static final PacketCodec<RegistryByteBuf, BoxUpdatePayload.Vec3b> VEC3B = PacketCodec.tuple(
			PacketCodecs.BYTE,
			BoxUpdatePayload.Vec3b::x,
			PacketCodecs.BYTE,
			BoxUpdatePayload.Vec3b::y,
			PacketCodecs.BYTE,
			BoxUpdatePayload.Vec3b::z,
			BoxUpdatePayload.Vec3b::new
	);
}
