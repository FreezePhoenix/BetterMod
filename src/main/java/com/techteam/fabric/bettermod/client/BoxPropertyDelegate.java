package com.techteam.fabric.bettermod.client;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.network.PacketIdentifiers;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.GlobalPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BoxPropertyDelegate implements PropertyDelegate {
	final RoomControllerBlockEntity entity;
	BlockPos pos;

	public BoxPropertyDelegate(@NotNull RoomControllerBlockEntity roomController) {
		entity = roomController;
		pos = roomController.getPos();
	}

	@Override
	public int get(int index) {
		return switch (index) {
			case 0 -> (entity.maxX - pos.getX() - 1);
			case 1 -> (pos.getX() - entity.minX);
			case 2 -> (entity.maxY - pos.getY() - 1);
			case 3 -> (pos.getY() - entity.minY);
			case 4 -> (entity.maxZ - pos.getZ() - 1);
			case 5 -> (pos.getZ() - entity.minZ);
			case 6 -> entity.getVariantIndex();
			case 7 -> entity.getVariants();
			default -> 0;
		};
	}

	public void rerender() {
		if (entity.getWorld() instanceof ClientWorld clientWorld) {
			clientWorld.scheduleBlockRenders(
					ChunkSectionPos.getSectionCoord(pos.getX()),
					ChunkSectionPos.getSectionCoord(pos.getY()),
					ChunkSectionPos.getSectionCoord(pos.getZ())
			);
		}
	}

	@Override
	public void set(int index, int value) {
		switch (index) {
			case 0:
				entity.maxX = pos.getX() + value + 1;
				updateBounds();
				break;
			case 1:
				entity.minX = pos.getX() - value;
				updateBounds();
			case 2:
				entity.maxY = pos.getY() + value + 1;
				updateBounds();
				break;
			case 3:
				entity.minY = pos.getY() - value;
				updateBounds();
				break;
			case 4:
				entity.maxZ = pos.getZ() + value + 1;
				updateBounds();
				break;
			case 5:
				entity.minZ = pos.getZ() - value;
				updateBounds();
				break;
			case 6:
				entity.setVariantIndex(value);
				rerender();
				break;
			case 7:
				break; // Cannot set number of variants.
		}
	}

	@Contract(pure = true)
	@Override
	public int size() {
		return 0;
	}

	public void sync() {
		if (entity.getWorld().isClient()) {
			PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
			data.writeGlobalPos(GlobalPos.create(entity.getWorld().getRegistryKey(), entity.getPos()));
			data.writeInt(entity.minX);
			data.writeInt(entity.minY);
			data.writeInt(entity.minZ);
			data.writeInt(entity.maxX);
			data.writeInt(entity.maxY);
			data.writeInt(entity.maxZ);
			data.writeInt(entity.getVariantIndex());
			ClientPlayNetworking.send(PacketIdentifiers.BOX_UPDATE_PACKET, data);
		}
	}

	public void updateBounds() {
		if (entity.getWorld().isClient()) {
			RoomTracker.updateRoom(entity.getUUID(), entity.minX, entity.minY, entity.minZ, entity.maxX, entity.maxY, entity.maxZ);
		}
	}
}
