package com.techteam.fabric.bettermod.impl.network;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.block.entity.RoomControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record BoxUpdatePayload(BlockPos pos, Vec3b min, Vec3b max, BlockState state) implements CustomPayload {

	public static final CustomPayload.Id<BoxUpdatePayload> ID = new CustomPayload.Id<>(Identifier.of("betterperf", "boxupdate"));
	public static final PacketCodec<RegistryByteBuf, BoxUpdatePayload> CODEC = PacketCodec.tuple(
			BlockPos.PACKET_CODEC, BoxUpdatePayload::pos,
			BetterMod.VEC3B, BoxUpdatePayload::min,
			BetterMod.VEC3B, BoxUpdatePayload::max,
			BetterMod.BLOCK_STATE, BoxUpdatePayload::state,
			BoxUpdatePayload::new
	);
	@Override
	public Id<BoxUpdatePayload> getId() {
		return ID;
	}
	public record Vec3b(byte x, byte y, byte z) {}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(ID,  CODEC);
		ServerPlayNetworking.registerGlobalReceiver(
				BoxUpdatePayload.ID,
				BoxUpdatePayload::handle
		);
	}

	private static void handle(BoxUpdatePayload payload, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			byte minX = payload.min().x();
			byte minY = payload.min().y();
			byte minZ = payload.min().z();
			byte maxX = payload.max().x();
			byte maxY = payload.max().y();
			byte maxZ = payload.max().z();
			BlockState state = payload.state();
			ServerWorld world = context.player().getServerWorld();
			if(world != null) {
				if(world.getBlockEntity(payload.pos()) instanceof RoomControllerBlockEntity roomController) {
					roomController.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
					roomController.setVariantState(state);
					roomController.markDirty();
				}
			}
		});
	}
}
