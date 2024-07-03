package com.techteam.fabric.bettermod.impl.client;

import com.techteam.fabric.bettermod.impl.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.impl.network.BoxUpdatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
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
		var properties = this.get().getBlock().getStateManager().getProperties();
		for(Property<?> property : properties) {
			if(index-- == 0) {
				return property;
			}
		}

		return null;
	}

	public int properties() {
		return this.get().getBlock().getStateManager().getProperties().size();
	}

	@Override
	public int get(int index) {
		return switch (index) {
			case 0 -> -entity.minX;
			case 1 -> entity.maxX - 1;
			case 2 -> -entity.minY;
			case 3 -> entity.maxY - 1;
			case 4 -> -entity.minZ;
			case 5 -> entity.maxZ - 1;
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
			clientWorld.updateListeners(entity.getPos(), entity.getCachedState(), entity.getCachedState(), 0);
		}
	}

	@Override
	public void set(int index, int value) {
		switch (index) {
			case 0 -> entity.minX = (byte) -value;
			case 1 -> entity.maxX = (byte) (value + 1);
			case 2 -> entity.minY = (byte) -value;
			case 3 -> entity.maxY = (byte) (value + 1);
			case 4 -> entity.minZ = (byte) -value;
			case 5 -> entity.maxZ = (byte) (value + 1);
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
		entity.updateRoom();
	}
}
