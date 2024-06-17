package com.techteam.fabric.bettermod.network;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

public record BoxUpdatePayload(GlobalPos globalPos, Vec3b min, Vec3b max, BlockState state) implements CustomPayload {

	public static final CustomPayload.Id<BoxUpdatePayload> ID = new CustomPayload.Id<>(Identifier.of("betterperf", "boxupdate"));
	public static final PacketCodec<RegistryByteBuf, BoxUpdatePayload> CODEC = PacketCodec.tuple(
			GlobalPos.PACKET_CODEC, BoxUpdatePayload::globalPos,
			BetterMod.VEC3B, BoxUpdatePayload::min,
			BetterMod.VEC3B, BoxUpdatePayload::max,
			BetterMod.BLOCK_STATE, BoxUpdatePayload::state,
			BoxUpdatePayload::new
	);
	@Override
	public Id<? extends CustomPayload> getId() {
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
		GlobalPos pos = payload.globalPos();
		RegistryKey<World> dimensionKey = pos.dimension();
		BlockPos blockPos = pos.pos();
		byte minX = payload.min().x();
		byte minY = payload.min().y();
		byte minZ = payload.min().z();
		byte maxX = payload.max().x();
		byte maxY = payload.max().y();
		byte maxZ = payload.max().z();
		BlockState state = payload.state();
		context.player().getServer().execute(() -> {
			ServerWorld world = context.player().getServer().getWorld(dimensionKey);
			if(world != null) {
				if(world.getBlockEntity(blockPos) instanceof RoomControllerBlockEntity roomController) {
					roomController.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
					roomController.setVariantState(state);
					roomController.markDirty();
				}
			}
		});
	}
}
