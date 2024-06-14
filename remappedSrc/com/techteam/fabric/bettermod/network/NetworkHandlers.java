package com.techteam.fabric.bettermod.network;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public final class NetworkHandlers {
	public static void initServerHandlers() {
		ServerPlayNetworking.registerGlobalReceiver(
				PacketIdentifiers.BOX_UPDATE_PACKET,
				NetworkHandlers::receive
		);
	}

	private static void receive(@NotNull MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, @NotNull PacketByteBuf data, PacketSender sender) {
		GlobalPos pos = data.readGlobalPos();
		RegistryKey<World> dimensionKey = pos.getDimension();
		BlockPos blockPos = pos.getPos();
		byte minX = data.readByte();
		byte minY = data.readByte();
		byte minZ = data.readByte();
		byte maxX = data.readByte();
		byte maxY = data.readByte();
		byte maxZ = data.readByte();
		BlockState state = data.readRegistryValue(Block.STATE_IDS);
		server.execute(() -> {
			ServerWorld world = server.getWorld(dimensionKey);
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
