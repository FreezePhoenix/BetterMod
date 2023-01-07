package com.techteam.fabric.bettermod.network;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class NetworkHandlers {
	public static void initServerHandlers() {
		ServerPlayNetworking.registerGlobalReceiver(
				PacketIdentifiers.BOX_UPDATE_PACKET,
				NetworkHandlers::receive
		);
	}

	private static void receive(@NotNull MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, @NotNull PacketByteBuf data, PacketSender sender) {
		BlockPos pos = data.readBlockPos();
		int minX = data.readInt();
		int minY = data.readInt();
		int minZ = data.readInt();
		int maxX = data.readInt();
		int maxY = data.readInt();
		int maxZ = data.readInt();
		int variant = data.readInt();
		server.execute(() -> {
			// TODO: Use GlobalPos instead of BlockPos?
			if (player.getWorld().getBlockEntity(pos) instanceof RoomControllerBlockEntity roomController) {
				roomController.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
				roomController.setVariantIndex(variant);
				roomController.markDirty();
			}
		});
	}
}
