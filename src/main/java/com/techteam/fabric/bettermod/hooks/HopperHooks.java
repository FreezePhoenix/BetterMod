package com.techteam.fabric.bettermod.hooks;

import com.techteam.fabric.bettermod.interfaces.ImprovedBlockEntityHopper;
import com.techteam.fabric.bettermod.interfaces.ImprovedBlockEntityHopper.HopperCache;
import com.techteam.fabric.bettermod.util.TypeFilterUnion;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class HopperHooks {
	private static final Int2ObjectArrayMap<Box[]> BOX_CACHE = new Int2ObjectArrayMap<>();
	private static final TypeFilterUnion<Entity, Entity> FILTER = TypeFilterUnion.Builder.create(Entity.class).add(
			TypeFilter.instanceOf(ItemEntity.class)).add(TypeFilter.instanceOf(StorageMinecartEntity.class)).build();

	private static final ConcurrentHashMap<Class<?>, Box[]> SHAPES_CACHE_BLUNT = new ConcurrentHashMap<>();

	private static final ConcurrentHashMap<Class<?>, Box[]> SHAPES_CACHE_SHARP = new ConcurrentHashMap<>();

	private HopperHooks() {

	}

	private static Box[] adjustWithCache(Box @NotNull [] boxes, @NotNull Hopper hop) {
		final int size = boxes.length;
		final Box[] result = getBoxCache(size);
		final double x = hop.getHopperX() - 0.5;
		final double y = hop.getHopperY() - 0.5;
		final double z = hop.getHopperZ() - 0.5;
		for (int i = 0; i < size; i++) {
			result[i] = boxes[i].offset(x, y, z);
		}
		return result;
	}

	public static void buildCache(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ImprovedBlockEntityHopper hopper) {
		Direction direction = state.get(HopperBlock.FACING);
		final List<Entity> entities = world.getEntitiesByType(FILTER,
															  getShape(hopper, direction).offset(pos),
															  EntityPredicates.VALID_ENTITY
															 );
		final HopperCache cache = hopper.getCache();
		if (!hopper.isEmpty()) {
			final int x = pos.getX() + direction.getOffsetX(), y = pos.getY() + direction.getOffsetY(), z = pos.getZ() + direction.getOffsetZ();
			final Box outputBox = new Box(x, y, z, x + 1, y + 1, z + 1);
			if (!hopper.invokeIsFull()) {
				final Box[] inputBoxes = adjustWithCache(getSharpInputShape(hopper), hopper);
				final Box inputBoxBlunt = new Box(
						pos.getX(),
						pos.getY(),
						pos.getZ(),
						pos.getX() + 1,
						pos.getY() + 2,
						pos.getZ() + 1
				);
				for (final Entity ent : entities) {
					if (ent instanceof final @NotNull ItemEntity item) {
						final Box itemEntityBox = ent.getBoundingBox();
						for (Box element : inputBoxes) {
							if (itemEntityBox.intersects(element)) {
								cache.ITEMS.add(item);
								break;
							}
						}
					} else if (ent instanceof final @NotNull Inventory inventory) {
						final Box inventoryBox = ent.getBoundingBox();
						if (inventoryBox.intersects(inputBoxBlunt)) {
							cache.INPUT_INV.add(inventory);
						} else if (inventoryBox.intersects(outputBox)) {
							cache.OUTPUT_INV.add(inventory);
						}
					}
				}
			} else {
				for (final Entity ent : entities) {
					if (ent instanceof final @NotNull Inventory inventory) {
						final Box inventoryBox = ent.getBoundingBox();
						if (inventoryBox.intersects(outputBox)) {
							cache.OUTPUT_INV.add(inventory);
						}
					}
				}
			}
		} else {
			if (!hopper.invokeIsFull()) {
				final Box[] inputBoxes = adjustWithCache(getSharpInputShape(hopper), hopper);
				final Box inputBoxBlunt = new Box(
						pos.getX(),
						pos.getY(),
						pos.getZ(),
						pos.getX() + 1,
						pos.getY() + 2,
						pos.getZ() + 1
				);
				for (final Entity ent : entities) {
					if (ent instanceof final @NotNull ItemEntity item) {
						final Box itemEntityBox = item.getBoundingBox();
						for (Box element : inputBoxes) {
							if (itemEntityBox.intersects(element)) {
								cache.ITEMS.add(item);
								break;
							}
						}
					} else if (ent instanceof final @NotNull Inventory inventory) {
						final Box inventoryBox = ent.getBoundingBox();
						if (inventoryBox.intersects(inputBoxBlunt)) {
							cache.INPUT_INV.add(inventory);
						}
					}
				}
			}
		}
	}

	public static boolean extractItemHook(final @NotNull World world, final Hopper hopper) {
		if (hopper instanceof ImprovedBlockEntityHopper improvedBlockEntityHopper) {
			for (ItemEntity item : improvedBlockEntityHopper.getCache().ITEMS) {
				if (HopperBlockEntity.extract(hopper, item)) {
					return true;
				}
			}
			return false;
		}
		// Simplify so we only iterate entities once globally.
		List<ItemEntity> items = world.getEntitiesByType(EntityType.ITEM,
														 getShape(
																 hopper,
																 Direction.UP
																 ).offset(hopper.getHopperX() - 0.5,
																		  hopper.getHopperY() - 0.5,
																		  hopper.getHopperZ() - 0.5
																		 ),
														 EntityPredicates.VALID_ENTITY
														);
		if (items.isEmpty()) {
			return false;
		}
		final Box[] boxes = adjustWithCache(getSharpInputShape(hopper), hopper);
		for (ItemEntity item : items) {
			Box itemEntityBox = item.getBoundingBox();
			for (Box element : boxes) {
				if (itemEntityBox.intersects(element) && HopperBlockEntity.extract(hopper, item)) {
					return true;
				}
			}
		}
		return false;
	}

	private static Box[] getBoxCache(int size) {
		return BOX_CACHE.computeIfAbsent(size, value -> new Box[size]);
	}

	public static @Nullable Inventory getInputInventoryHook(@NotNull World world, @NotNull Hopper hopper) {
		double x = hopper.getHopperX(), y = hopper.getHopperY() + 1.0, z = hopper.getHopperZ();
		BlockPos blockPos = new BlockPos(x, y, z);
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block instanceof InventoryProvider provider) {
			return provider.getInventory(blockState, world, blockPos);
		}
		if (blockState.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Inventory inventory) {
				if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock chestBlock) {
					return ChestBlock.getInventory(chestBlock, blockState, world, blockPos, true);
				}
				return inventory;
			}
		}
		if (hopper instanceof ImprovedBlockEntityHopper improv) {
			List<Inventory> invs = improv.getCache().INPUT_INV;
			if (!invs.isEmpty()) {
				return invs.get(world.random.nextInt(invs.size()));
			}
		} else {
			List<Entity> invs = world.getOtherEntities(null,
													   new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5),
													   EntityPredicates.VALID_INVENTORIES
													  );
			if (!invs.isEmpty()) {
				return (Inventory) invs.get(world.random.nextInt(invs.size()));
			}
		}
		return null;
	}

	public static @Nullable Inventory getOutputInventoryHook(@NotNull World world, Hopper hopper, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block instanceof InventoryProvider provider) {
			return provider.getInventory(blockState, world, blockPos);
		}
		if (blockState.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof Inventory inventory) {
				if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock chestBlock) {
					return ChestBlock.getInventory(chestBlock, blockState, world, blockPos, true);
				}
				return inventory;
			}
		}
		if (hopper instanceof ImprovedBlockEntityHopper improv) {
			List<Inventory> invs = improv.getCache().OUTPUT_INV;
			if (!invs.isEmpty()) {
				return invs.get(world.random.nextInt(invs.size()));
			}
		}
		return null;
	}

	private static Box getShape(@NotNull Hopper hop, @NotNull Direction direction) {
		return SHAPES_CACHE_BLUNT.computeIfAbsent(hop.getClass(), t -> {
			final int size = Direction.values().length;
			Box[] shapes = new Box[size];
			for (int i = 0; i < size; i++) {
				Direction cur = Direction.byId(i);
				shapes[i] = VoxelShapes.union(hop.getInputAreaShape(),
											  VoxelShapes.fullCube()
														 .offset(cur.getOffsetX(), cur.getOffsetY(), cur.getOffsetZ())
											 ).getBoundingBox();
			}
			return shapes;
		})[direction.getId()];
	}

	private static Box[] getSharpInputShape(@NotNull Hopper hop) {
		return SHAPES_CACHE_SHARP.computeIfAbsent(hop.getClass(), (final var t) -> {
			ObjectArrayList<Box> boxes = new ObjectArrayList<>();
			hop.getInputAreaShape().forEachBox((x1, y1, z1, x2, y2, z2) -> {
				boxes.add(new Box(x1, y1, z1, x2, y2, z2));
			});
			return boxes.toArray(Box[]::new);
		});
	}

	public static boolean isEmptyHook(final @NotNull DefaultedList<ItemStack> items) {
		int index = items.size();
		while (index-- > 0) {
			if (!items.get(index).isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
