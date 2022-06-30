package com.techteam.fabric.bettermod.network;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;

import static com.techteam.fabric.bettermod.network.PacketIdentifiers.BOX_UPDATE_PACKET;
import static net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver;

public final class NetworkHandlers {
	public static void initServerHandlers() {
		registerGlobalReceiver(
				BOX_UPDATE_PACKET,
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
			if (player.getWorld().getBlockEntity(pos) instanceof RoomControllerBlockEntity roomController) {
				roomController.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
				roomController.setVariant(variant);
				roomController.markDirty();
			}
		});
	}
}
