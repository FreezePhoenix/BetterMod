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
	private final int x;
	private final int y;
	private final int z;

	public BoxPropertyDelegate(@NotNull RoomControllerBlockEntity roomController, BlockPos pos) {
		this.entity = roomController;
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}

	@Override
	public int get(int index) {
		return switch (index) {
			case 0 -> entity.maxX - 1;
			case 1 -> -entity.minX;
			case 2 -> entity.maxY - 1;
			case 3 -> -entity.minY;
			case 4 -> entity.maxZ - 1;
			case 5 -> -entity.minZ;
			case 6 -> entity.getVariantIndex();
			case 7 -> entity.getVariants();
			default -> 0;
		};
	}

	public void rerender() {
		if (entity.getWorld() instanceof ClientWorld clientWorld) {
			clientWorld.scheduleBlockRenders(
					ChunkSectionPos.getSectionCoord(x),
					ChunkSectionPos.getSectionCoord(y),
					ChunkSectionPos.getSectionCoord(z)
			);
		}
	}

	@Override
	public void set(int index, int value) {
		switch (index) {
			case 0:
				entity.maxX = (byte) (value + 1);
				updateBounds();
				break;
			case 1:
				entity.minX = (byte) -value;
				updateBounds();
			case 2:
				entity.maxY = (byte) (value + 1);
				updateBounds();
				break;
			case 3:
				entity.minY = (byte) -value;
				updateBounds();
				break;
			case 4:
				entity.maxZ = (byte) (value + 1);
				updateBounds();
				break;
			case 5:
				entity.minZ = (byte) -value;
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
			BlockPos pos = entity.getPos();
			data.writeGlobalPos(GlobalPos.create(entity.getWorld().getRegistryKey(), pos));

			data.writeByte(entity.minX);
			data.writeByte(entity.minY);
			data.writeByte(entity.minZ);
			data.writeByte(entity.maxX);
			data.writeByte(entity.maxY);
			data.writeByte(entity.maxZ);
			data.writeInt(entity.getVariantIndex());
			ClientPlayNetworking.send(PacketIdentifiers.BOX_UPDATE_PACKET, data);
		}
	}

	public void updateBounds() {
		if (entity.getWorld().isClient()) {
			RoomTracker.updateRoom(entity.getUUID(), entity.minX + x, entity.minY + y, entity.minZ + z, entity.maxX + x, entity.maxY + y, entity.maxZ + z);
		}
	}
}
