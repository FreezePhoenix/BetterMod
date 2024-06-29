package com.techteam.fabric.bettermod.client;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.api.hooks.IRoomCaching;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class RoomTracker {
	private static final HashMap<UUID, Room> UUID_ROOM_HASH_MAP = new HashMap<>();
	private static final Collection<Room> ROOM_COLLECTION = UUID_ROOM_HASH_MAP.values();
	private static final ReadWriteLock ROOM_HASH_MAP_LOCK = new ReentrantReadWriteLock();
	private static @Nullable Room currentRoom = null;
	private static int nullRoomStamp = 0;

	@Environment(EnvType.CLIENT)
	@Contract("_ -> new")
	public static @NotNull RoomTrackerTicker bind(@NotNull IRoomCaching playerEntity) {
		if (BetterMod.CONFIG.LogRoomAllocations) {
			BetterMod.LOGGER.info("Bound new RoomTrackerTicker to player.");
		}
		currentRoom = null;
		nullRoomStamp = 0;
		return new RoomTrackerTicker(playerEntity);
	}

	public static int getNullRoomStamp() {
		return nullRoomStamp;
	}

	@Contract(pure = true)
	public static @Nullable Room getActiveRoom() {
		return currentRoom;
	}

	public static @Nullable Room getOrUpdateRoom(@NotNull IRoomCaching roomCaching) {
		Room cachedRoom = roomCaching.betterMod$getRoom();
		if(cachedRoom == null) {
			if(roomCaching.betterMod$getStamp() == nullRoomStamp) {
				// null room cannot be removed.
				return null;
			} else {
				cachedRoom = getRoomForPos(roomCaching.betterMod$blockPos());
				roomCaching.betterMod$setRoom(cachedRoom);
				if(cachedRoom == null) {
					roomCaching.betterMod$setStamp(nullRoomStamp);
				} else {
					roomCaching.betterMod$setStamp(cachedRoom.modificationStamp);
				}
			}
		}  else {
			if(roomCaching.betterMod$getStamp() == cachedRoom.modificationStamp) {
				// Even if the entity is still in the room, the room may be removed. If it is removed, recalculate which room they should be in.
				if(cachedRoom.removed) {
					cachedRoom = getRoomForPos(roomCaching.betterMod$blockPos());
					roomCaching.betterMod$setRoom(cachedRoom);
					if(cachedRoom == null) {
						roomCaching.betterMod$setStamp(nullRoomStamp);
					} else {
						roomCaching.betterMod$setStamp(cachedRoom.modificationStamp);
					}
				}
			} else if(cachedRoom.contains(roomCaching.betterMod$blockPos())) {
				roomCaching.betterMod$setStamp(cachedRoom.modificationStamp);
			} else {
				cachedRoom = getRoomForPos(roomCaching.betterMod$blockPos());
				roomCaching.betterMod$setRoom(cachedRoom);
				if(cachedRoom == null) {
					roomCaching.betterMod$setStamp(nullRoomStamp);
				} else {
					roomCaching.betterMod$setStamp(cachedRoom.modificationStamp);
				}
			}
		}
		return cachedRoom;
	}
	public static @Nullable Room getRoomForPos(@NotNull Vec3i pos) {
		final int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		try {
			ROOM_HASH_MAP_LOCK.readLock().lock();
			for (final Room room : ROOM_COLLECTION) {
				if (room.contains(x, y, z)) {
					return room;
				}
			}
			return null;
		} finally {
			ROOM_HASH_MAP_LOCK.readLock().unlock();
		}
	}

	public static void removeRoom(@NotNull UUID uuid) {
		ROOM_HASH_MAP_LOCK.writeLock().lock();
		UUID_ROOM_HASH_MAP.remove(uuid).markRemoved();
		ROOM_HASH_MAP_LOCK.readLock().lock();
		ROOM_HASH_MAP_LOCK.writeLock().unlock();
		if (BetterMod.CONFIG.LogRoomAllocations) {
			BetterMod.LOGGER.info("Room removed: {}. New room count: {}", uuid, UUID_ROOM_HASH_MAP.size());
		}
		ROOM_HASH_MAP_LOCK.readLock().unlock();
	}

	public static void addRoom(@NotNull UUID id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		ROOM_HASH_MAP_LOCK.writeLock().lock();
		UUID_ROOM_HASH_MAP.put(id, new Room(id, minX, minY, minZ, maxX, maxY, maxZ));
		ROOM_HASH_MAP_LOCK.readLock().lock();
		ROOM_HASH_MAP_LOCK.writeLock().unlock();
		if (BetterMod.CONFIG.LogRoomAllocations) {
			BetterMod.LOGGER.info("Room added: {}. New room count: {}", id, UUID_ROOM_HASH_MAP.size());
		}
		ROOM_HASH_MAP_LOCK.readLock().unlock();
	}

	public static void updateRoom(@NotNull UUID id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		ROOM_HASH_MAP_LOCK.writeLock().lock();
		if(UUID_ROOM_HASH_MAP.containsKey(id)) {
			UUID_ROOM_HASH_MAP.get(id).setBounds(minX, minY, minZ, maxX, maxY, maxZ);
			if (BetterMod.CONFIG.LogRoomAllocations) {
				BetterMod.LOGGER.info("Room updated: {}", id);
			}
		} else {
			UUID_ROOM_HASH_MAP.put(id, new Room(id, minX, minY, minZ, maxX, maxY, maxZ));
			if (BetterMod.CONFIG.LogRoomAllocations) {
				BetterMod.LOGGER.info("Room added: {}. New room count: {}", id, UUID_ROOM_HASH_MAP.size());
			}
		}
		ROOM_HASH_MAP_LOCK.writeLock().unlock();
	}

	@Environment(EnvType.CLIENT)
	static public final class Room {
		private final UUID id;
		public int minX;
		public int minY;
		public int minZ;
		public int maxX;
		public int maxY;
		public int maxZ;
		private int modificationStamp = 0;
		public boolean removed = false;
		public int getStamp() {
			return modificationStamp;
		}

		@Contract(pure = true)
		public Room(@NotNull UUID id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
			this.id = id;
			this.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
		}

		@Contract(pure = true)
		public boolean contains(@NotNull Vec3i pos) {
			return contains(pos.getX(), pos.getY(), pos.getZ());
		}

		@Contract(pure = true)
		public boolean contains(int x, int y, int z) {
			return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
		}

		@Contract(value = "null -> false",
		          pure = true)
		@Override
		public boolean equals(Object o) {
			return o instanceof Room room && room.id.equals(id);
		}

		@Contract(pure = true)
		public UUID getUUID() {
			return id;
		}

		@Contract(pure = true)
		@Override
		public int hashCode() {
			return id.hashCode();
		}

		public void setBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
			// When the room's bounds are changed, we need to increment the stamps for this room.
			this.modificationStamp++;
			// We also need to increment the stamp for the null (global) room, so that all IRoomCache not in a room also check for their new room.
			nullRoomStamp++;
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}
		public void markRemoved() {
			// When the room's bounds are changed, we need to increment the stamps for this room.
			this.modificationStamp++;
			// We also need to increment the stamp for the null (global) room, so that all IRoomCache not in a room also check for their new room.
			nullRoomStamp++;
			this.removed = true;
		}

		@Contract(pure = true)
		@Override
		public @NotNull String toString() {
			return id.toString();
		}
	}

	@Environment(EnvType.CLIENT)
	static public final class RoomTrackerTicker implements ClientPlayerTickable {
		private final IRoomCaching clientPlayer;

		@Contract(pure = true)
		RoomTrackerTicker(IRoomCaching player) {
			this.clientPlayer = player;
		}

		@Override
		public void tick() {
			currentRoom = getOrUpdateRoom(clientPlayer);
		}
	}
}