package com.techteam.fabric.bettermod.client;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.network.PacketIdentifiers;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.NotNull;

import static com.techteam.fabric.bettermod.client.RoomTracker.updateRoom;

@Environment(EnvType.CLIENT)
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
			case 0 -> (int) (entity.getBounds().maxX - pos.getX() - 1);
			case 1 -> (int) (pos.getX() - entity.getBounds().minX);
			case 2 -> (int) (entity.getBounds().maxY - pos.getY() - 1);
			case 3 -> (int) (pos.getY() - entity.getBounds().minY);
			case 4 -> (int) (entity.getBounds().maxZ - pos.getZ() - 1);
			case 5 -> (int) (pos.getZ() - entity.getBounds().minZ);
			case 6 -> entity.getVariant();
			case 7 -> entity.getVariants();
			default -> 0;
		};
	}

	public void rerender() {
		if (entity.getWorld()
		          .isClient()) {
			((ClientWorld) entity.getWorld()).scheduleBlockRenders(
					ChunkSectionPos.getSectionCoord(pos.getX()),
					ChunkSectionPos.getSectionCoord(pos.getY()),
					ChunkSectionPos.getSectionCoord(pos.getZ())
			);
		}
	}

	@Override
	public void set(int index, int value) {
		Box new_bounds = entity.getBounds();
		int maxX = (int) new_bounds.maxX;
		int maxY = (int) new_bounds.maxY;
		int maxZ = (int) new_bounds.maxZ;
		int minX = (int) new_bounds.minX;
		int minY = (int) new_bounds.minY;
		int minZ = (int) new_bounds.minZ;
		switch (index) {
			case 0:
				new_bounds = new Box(minX, minY, minZ, pos.getX() + value + 1, maxY, maxZ);
				updateBounds(new_bounds);
				break;
			case 1:
				new_bounds = new Box(pos.getX() - value, minY, minZ, maxX, maxY, maxZ);
				updateBounds(new_bounds);
				break;
			case 2:
				new_bounds = new Box(minX, minY, minZ, maxX, pos.getY() + value + 1, maxZ);
				updateBounds(new_bounds);
				break;
			case 3:
				new_bounds = new Box(minX, pos.getY() - value, minZ, maxX, maxY, maxZ);
				updateBounds(new_bounds);
				break;
			case 4:
				new_bounds = new Box(minX, minY, minZ, maxX, maxY, pos.getZ() + value + 1);
				updateBounds(new_bounds);
				break;
			case 5:
				new_bounds = new Box(minX, minY, pos.getZ() - value, maxX, maxY, maxZ);
				updateBounds(new_bounds);
				break;
			case 6:
				entity.setVariant(value);
				rerender();
				break;
			case 7:
				break; // Cannot set number of variants.
		}
	}

	@Override
	public int size() {
		return 0;
	}

	public void sync() {
		if (entity.getWorld()
		          .isClient()) {
			Box bounds = entity.getBounds();
			PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
			data.writeBlockPos(pos);
			data.writeInt((int) bounds.minX);
			data.writeInt((int) bounds.minY);
			data.writeInt((int) bounds.minZ);
			data.writeInt((int) bounds.maxX);
			data.writeInt((int) bounds.maxY);
			data.writeInt((int) bounds.maxZ);
			data.writeInt(entity.getVariant());
			ClientPlayNetworking.send(PacketIdentifiers.BOX_UPDATE_PACKET, data);
		}
	}

	public void updateBounds(@NotNull Box bounds) {
		entity.setBounds(bounds);
		if (entity.getWorld()
		          .isClient()) {
			updateRoom(entity, bounds);
		}
	}
}
