package com.techteam.fabric.bettermod.client;

import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.network.BoxUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
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

	public Property<?> getProperty(int index) {
		var properties = this.get().getProperties();

		for(Property<?> property : properties) {
			if(index-- == 0) {
				return property;
			}
		}

		return null;
	}

	public int properties() {
		return this.get().getProperties().size();
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
			default -> throw new IllegalStateException("Unexpected value: " + index);
		};
	}
	public BlockState get() {
		return entity.getVariantState();
	}

	public void set(BlockState state) {
		entity.setVariantState(state);
		rerender();
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
			case 0 -> entity.maxX = (byte) (value + 1);
			case 1 -> entity.minX = (byte) -value;
			case 2 -> entity.maxY = (byte) (value + 1);
			case 3 -> entity.minY = (byte) -value;
			case 4 -> entity.maxZ = (byte) (value + 1);
			case 5 -> entity.minZ = (byte) -value;
			default -> throw new IllegalStateException("Unexpected value: " + index);
		}
		updateBounds();
	}

	@Contract(pure = true)
	@Override
	public int size() {
		return 0;
	}

	public void sync() {
		if (entity.getWorld() instanceof ClientWorld) {
			ClientPlayNetworking.send(new BoxUpdatePayload(
					entity.getPos(),
					new BoxUpdatePayload.Vec3b(entity.minX, entity.minY, entity.minZ),
					new BoxUpdatePayload.Vec3b(entity.maxX, entity.maxY, entity.maxZ),
					entity.getVariantState()
			));
		}
	}

	public void updateBounds() {
		if (entity.getWorld() instanceof ClientWorld) {
			RoomTracker.updateRoom(entity.getUUID(), entity.minX + x, entity.minY + y, entity.minZ + z, entity.maxX + x, entity.maxY + y, entity.maxZ + z);
		}
	}
}
