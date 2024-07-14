package com.techteam.fabric.bettermod.impl.network;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.impl.client.gui.RoomControllerScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record BoxUpdatePayload(BlockPos pos, Vec3b min, Vec3b max, BlockState state) implements CustomPayload {

	public static final CustomPayload.Id<BoxUpdatePayload> ID = new CustomPayload.Id<>(Identifier.of("bettermod", "boxupdate"));
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
	public record Vec3b(byte x, byte y, byte z) {
		public static Vec3b ORIGIN = new Vec3b((byte) 0, (byte) 0, (byte) 0);
	}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(ID, CODEC);
		PayloadTypeRegistry.playS2C().register(ID, CODEC);
		ServerPlayNetworking.registerGlobalReceiver(
				BoxUpdatePayload.ID,
				BoxUpdatePayload::handle
		);
	}

	@Environment(EnvType.CLIENT)
	public static void registerClient() {
		ClientPlayNetworking.registerGlobalReceiver(
				BoxUpdatePayload.ID,
				BoxUpdatePayload::handle
		);
	}

	@Environment(EnvType.CLIENT)
	private static void handle(BoxUpdatePayload payload, ClientPlayNetworking.Context context) {
		context.client().execute(() -> {
			if(context.player().currentScreenHandler instanceof RoomControllerScreenHandler roomControllerScreenHandler) {
				roomControllerScreenHandler.update(payload);
			}
		});
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
					world.getChunkManager().markForUpdate(payload.pos());
					for (ServerPlayerEntity player : PlayerLookup.tracking(roomController)) {
						if (!player.equals(context.player()) && player.currentScreenHandler instanceof RoomControllerScreenHandler roomControllerScreenHandler) {
							if (roomControllerScreenHandler.pos.equals(payload.pos())) {
								ServerPlayNetworking.send(player, payload);
							}
						}
					}
				}
			}
		});
	}
}
