package com.techteam.fabric.bettermod.client;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
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

    public static void updateRoom(final @NotNull RoomControllerBlockEntity roomController, final Box bounds) {
        ROOM_HASH_MAP_LOCK.writeLock().lock();
        UUID_ROOM_HASH_MAP.compute(roomController.getUUID(), (final var uuid, final var room) -> {
            if (bounds == null) {
                if (room == null) {
                    BetterMod.LOGGER.warn("Duplicate room removal: " + uuid);
                    BetterMod.LOGGER.warn("Room count: " + (UUID_ROOM_HASH_MAP.size()));
                } else {
                    BetterMod.LOGGER.info("Room removed: " + uuid);
                    BetterMod.LOGGER.info("Room count: " + (UUID_ROOM_HASH_MAP.size() - 1));
                }
                return null;
            }
            if (room == null) {
                BetterMod.LOGGER.info("Room added: " + uuid);
                BetterMod.LOGGER.info("Room count: " + (UUID_ROOM_HASH_MAP.size() + 1));
                return new Room(roomController);
            }
            BetterMod.LOGGER.info("Room updated: " + uuid);
            return room.setBounds(bounds);
        });
        ROOM_HASH_MAP_LOCK.writeLock().unlock();
    }

    @Environment(EnvType.CLIENT)
    static public final class Room {
        private final UUID id;
        private Box bounds;

        public Room(@NotNull RoomControllerBlockEntity roomController) {
            this.id = roomController.getUUID();
            this.setBounds(roomController.getBounds());
        }

        public boolean contains(@NotNull Vec3f pos) {
            return contains(pos.getX(), pos.getY(), pos.getZ());
        }

        public boolean contains(@NotNull Vec3i pos) {
            return contains(pos.getX(), pos.getY(), pos.getZ());
        }

        public boolean contains(@NotNull Vec3d pos) {
            return contains(pos.x, pos.y, pos.z);
        }

        public boolean contains(double x, double y, double z) {
            return bounds.contains(x, y, z);
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

        @Contract(value = "_ -> this",
                  mutates = "this")
        public Room setBounds(Box bounds) {
            this.bounds = bounds;
            return this;
        }

        @Override
        public @NotNull String toString() {
            return id + " = " + bounds;
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