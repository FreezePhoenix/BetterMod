package com.techteam.fabric.bettermod.client;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Environment(EnvType.CLIENT)
public final class RoomTracker {
    private static final HashMap<UUID, Room> UUID_ROOM_HASH_MAP = new HashMap<>();
    private static final Collection<Room> ROOM_COLLECTION = UUID_ROOM_HASH_MAP.values();
    private static final ReadWriteLock ROOM_HASH_MAP_LOCK = new ReentrantReadWriteLock();
    private static @Nullable Room currentRoom = null;

    public static @NotNull RoomTrackerTicker bind(@NotNull AbstractClientPlayerEntity clientPlayerEntity) {
        BetterMod.LOGGER.info("Bound new RoomTrackerTicker to player: " + clientPlayerEntity.getEntityName() + "[" + clientPlayerEntity.getUuidAsString() + "]");
        return new RoomTrackerTicker(clientPlayerEntity);
    }

    @Contract(pure = true)
    public static @Nullable Room getActiveRoom() {
        return currentRoom;
    }

    public static Room getRoomForBlockEntity(@NotNull BlockEntity blockEntity) {
        if(blockEntity instanceof RoomControllerBlockEntity roomControllerBlockEntity) {
            ROOM_HASH_MAP_LOCK.readLock().lock();
            Room room = UUID_ROOM_HASH_MAP.getOrDefault(roomControllerBlockEntity.getUUID(), null);
            ROOM_HASH_MAP_LOCK.readLock().unlock();
            return room;
        }
        return getRoomForPos(blockEntity.getPos());
    }

    public static Room getRoomForEntity(@NotNull Entity entity) {
        return getRoomForPos(entity.getPos());
    }

    private static @Nullable Room getRoomForPos(@NotNull Vec3d pos) {
        final double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        ROOM_HASH_MAP_LOCK.readLock().lock();
        for (final Room room : ROOM_COLLECTION) {
            if (room.contains(x, y, z)) {
                ROOM_HASH_MAP_LOCK.readLock().unlock();
                return room;
            }
        }
        ROOM_HASH_MAP_LOCK.readLock().unlock();
        return null;
    }

    private static @Nullable Room getRoomForPos(@NotNull Vec3i pos) {
        final double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        ROOM_HASH_MAP_LOCK.readLock().lock();
        for (final Room room : UUID_ROOM_HASH_MAP.values()) {
            if (room.contains(x, y, z)) {
                ROOM_HASH_MAP_LOCK.readLock().unlock();
                return room;
            }
        }
        ROOM_HASH_MAP_LOCK.readLock().unlock();
        return null;
    }

    public static void removeRoom(final @NotNull RoomControllerBlockEntity roomController) {
        ROOM_HASH_MAP_LOCK.writeLock().lock();
        UUID_ROOM_HASH_MAP.remove(roomController.getUUID());
        ROOM_HASH_MAP_LOCK.readLock().lock();
        ROOM_HASH_MAP_LOCK.writeLock().unlock();
        BetterMod.LOGGER.info("Room removed: " + roomController.getUUID());
        BetterMod.LOGGER.info("Room count: " + UUID_ROOM_HASH_MAP.size());
        ROOM_HASH_MAP_LOCK.readLock().unlock();
    }

    public static void addRoom(@NotNull UUID id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        ROOM_HASH_MAP_LOCK.writeLock().lock();
        UUID_ROOM_HASH_MAP.put(id, new Room(id, minX, minY, minZ, maxX, maxY, maxZ));
        ROOM_HASH_MAP_LOCK.readLock().lock();
        ROOM_HASH_MAP_LOCK.writeLock().unlock();
        BetterMod.LOGGER.info("Room added: " + id);
        BetterMod.LOGGER.info("Room count: " + UUID_ROOM_HASH_MAP.size());
        ROOM_HASH_MAP_LOCK.readLock().unlock();
    }

    public static void updateRoom(@NotNull UUID id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        ROOM_HASH_MAP_LOCK.readLock().lock();
        UUID_ROOM_HASH_MAP.get(id).setBounds(minX, minY, minZ, maxX, maxY, maxZ);
        ROOM_HASH_MAP_LOCK.readLock().unlock();
        BetterMod.LOGGER.info("Room updated: " + id);
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
        public Room(@NotNull UUID id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.id = id;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public boolean contains(@NotNull Vec3i pos) {
            return contains(pos.getX(), pos.getY(), pos.getZ());
        }

        public boolean contains(@NotNull Vec3d pos) {
            return contains(pos.x, pos.y, pos.z);
        }
        public boolean contains(int x, int y, int z) {
            return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
        }
        public boolean contains(double x, double y, double z) {
            return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Room && ((Room) o).id.equals(id);
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

        @Contract(mutates = "this")
        public void setBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        @Override
        public @NotNull String toString() {
            return id.toString();
        }
    }

    static public final class RoomTrackerTicker implements ClientPlayerTickable {
        private final AbstractClientPlayerEntity clientPlayer;

        RoomTrackerTicker(AbstractClientPlayerEntity player) {
            this.clientPlayer = player;
        }

        @Override
        public void tick() {
            if (currentRoom == null) {
                Room room = RoomTracker.getRoomForEntity(clientPlayer);
                if (room != null) {
                    if (currentRoom != null) {
                        BetterMod.LOGGER.info("Exiting room: " + currentRoom.getUUID());
                    }
                    BetterMod.LOGGER.info("Entering room: " + room.getUUID());
                    currentRoom = room;
                }
            } else if (!currentRoom.contains(clientPlayer.getPos())) {
                Room room = RoomTracker.getRoomForEntity(clientPlayer);
                if (room != currentRoom) {
                    if (room != null) {
                        if (currentRoom != null) {
                            BetterMod.LOGGER.info("Exiting room: " + currentRoom.getUUID());
                        }
                        BetterMod.LOGGER.info("Entering room: " + room.getUUID());
                    } else {
                        BetterMod.LOGGER.info("Exiting room: " + currentRoom.getUUID());
                    }
                    currentRoom = room;
                }
            }
        }
    }
}