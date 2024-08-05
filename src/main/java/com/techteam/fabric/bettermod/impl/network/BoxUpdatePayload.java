package com.techteam.fabric.bettermod.impl.network;

import com.techteam.fabric.bettermod.impl.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.impl.client.gui.RoomControllerScreenHandler;
import com.techteam.fabric.bettermod.impl.util.Codecs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public record BoxUpdatePayload(BlockPos pos, Vec3b min, Vec3b max, BlockState state) implements CustomPayload {

	public static final CustomPayload.Id<BoxUpdatePayload> ID = new CustomPayload.Id<>(Identifier.of(
			"bettermod",
			"boxupdate"
	));
	public static final PacketCodec<RegistryByteBuf, BoxUpdatePayload> CODEC = PacketCodec.tuple(
			BlockPos.PACKET_CODEC, BoxUpdatePayload::pos,
			Codecs.VEC3B, BoxUpdatePayload::min,
			Codecs.VEC3B, BoxUpdatePayload::max,
			Codecs.BLOCK_STATE, BoxUpdatePayload::state,
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
			if (context.player().currentScreenHandler instanceof RoomControllerScreenHandler roomControllerScreenHandler) {
				roomControllerScreenHandler.update(payload);
			}
		});
	}

	private static void handle(BoxUpdatePayload payload, ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			byte minX = (byte) MathHelper.clamp(payload.min().x(), -63, 0);
			byte minY = (byte) MathHelper.clamp(payload.min().y(), -63, 0);
			byte minZ = (byte) MathHelper.clamp(payload.min().z(), -63, 0);
			byte maxX = (byte) MathHelper.clamp(payload.max().x(), 1, 64);
			byte maxY = (byte) MathHelper.clamp(payload.max().y(), 1, 64);
			byte maxZ = (byte) MathHelper.clamp(payload.max().z(), 1, 64);
			BlockState state = payload.state();
			ServerWorld world = context.player().getServerWorld();
			if (world != null) {
				if (world.getBlockEntity(payload.pos()) instanceof RoomControllerBlockEntity roomController) {
					roomController.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
					roomController.setVariantState(state);
					roomController.markDirty();
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
