package com.freezephoenix.fabric.bettermod.impl;

import com.freezephoenix.fabric.bettermod.api.block.BetterBlock;
import com.freezephoenix.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.freezephoenix.fabric.bettermod.impl.block.BetterBookshelfBlock;
import com.freezephoenix.fabric.bettermod.impl.block.BitHopperBlock;
import com.freezephoenix.fabric.bettermod.impl.block.PullHopperBlock;
import com.freezephoenix.fabric.bettermod.impl.block.StickHopperBlock;
import com.freezephoenix.fabric.bettermod.impl.block.entity.BetterBookshelfBlockEntity;
import com.freezephoenix.fabric.bettermod.impl.block.entity.BitHopperBlockEntity;
import com.freezephoenix.fabric.bettermod.impl.block.entity.PullHopperBlockEntity;
import com.freezephoenix.fabric.bettermod.impl.block.entity.StickHopperBlockEntity;
import com.freezephoenix.fabric.bettermod.impl.client.gui.BetterBookshelfScreenHandler;
import com.freezephoenix.fabric.bettermod.impl.client.gui.BetterScreen;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;


public class BetterMod implements ModInitializer, ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Collection<ItemLike> ITEMS = new ArrayList<>();
	public static Block ROOM_CONTROLLER_BLOCK;
	public static final CreativeModeTab ITEM_GROUP = FabricCreativeModeTab.builder()
																		  .icon(() -> new ItemStack(
																				  ROOM_CONTROLLER_BLOCK.asItem()))
																		  .title(Component.translatable(
																				  "bettermod.item_group"))
																		  .displayItems((_, entries) -> {
																			  for (ItemLike itemLike : ITEMS) {
																				  entries.accept(itemLike);
																			  }
																		  })
																		  .build();
	public static BetterBlock<BitHopperBlockEntity> BIT_HOPPER_BLOCK;
	public static BetterBlock<PullHopperBlockEntity> PULL_HOPPER_BLOCK;
	public static BetterBlock<StickHopperBlockEntity> STICK_HOPPER_BLOCK;
	public static BlockEntityType<BitHopperBlockEntity> BIT_HOPPER_BLOCK_ENTITY_TYPE;
	public static BlockEntityType<PullHopperBlockEntity> PULL_HOPPER_BLOCK_ENTITY_TYPE;
	public static BlockEntityType<StickHopperBlockEntity> STICK_HOPPER_BLOCK_ENTITY_TYPE;
	public static BlockEntityType<BetterBookshelfBlockEntity> BOOKSHELF_BLOCK_ENTITY_TYPE;
	public static MenuType<BetterBookshelfScreenHandler> BOOKSHELF_SCREEN_HANDLER_TYPE;
	public static Item SLING_MECHANISM;

	public static <T extends Block> T registerBlock(BlockItemId ID, Function<BlockBehaviour.Properties, T> factory, Block template) {
		ResourceKey<Block> blockRegistryKey = ID.block();
		T block = Registry.register(
				BuiltInRegistries.BLOCK,
				blockRegistryKey,
				factory.apply(BlockBehaviour.Properties.ofFullCopy(template).setId(blockRegistryKey))
		);
		registerItem(ID, BlockItem::new, block);
		return block;
	}

	public static <T extends Item> T registerItem(Identifier ID, Function<Item.Properties, T> factory) {
		ResourceKey<Item> itemRegistryKey = ResourceKey.create(Registries.ITEM, ID);
		T new_item = Registry.register(
				BuiltInRegistries.ITEM,
				itemRegistryKey,
				factory.apply(new Item.Properties().setId(itemRegistryKey))
		);
		ITEMS.add(new_item);
		return new_item;
	}

	public static <T extends Item, A> T registerItem(BlockItemId ID, BiFunction<A, Item.Properties, T> factory, A arg) {
		ResourceKey<Item> itemRegistryKey = ID.item();
		T new_item = Registry.register(
				BuiltInRegistries.ITEM,
				itemRegistryKey,
				factory.apply(arg, new Item.Properties().setId(itemRegistryKey))
		);
		ITEMS.add(new_item);
		return new_item;
	}

	public static <E extends BetterBlockEntity<E>> BlockEntityType<E> registerBlockEntityType(Identifier ID, BetterBlock<E> block, FabricBlockEntityTypeBuilder.Factory<E> factory) {
		return block.blockEntityType = Registry.register(
				BuiltInRegistries.BLOCK_ENTITY_TYPE,
				ID,
				FabricBlockEntityTypeBuilder.create(factory, block).build()
		);
	}

	public static <T extends AbstractContainerMenu> MenuType<T> registerScreenHandler(Identifier ID, MenuType.MenuSupplier<T> factory) {
		return Registry.register(BuiltInRegistries.MENU, ID, new MenuType<>(factory, FeatureFlagSet.of()));
	}

	@Environment(EnvType.CLIENT)
	private static <T extends SyncedGuiDescription> void registerScreen(MenuType<T> screenHandlerType) {
		MenuScreens.register(screenHandlerType, BetterScreen<T>::new);
	}

	@Override
	public void onInitialize() {
		Registry.register(
				BuiltInRegistries.CREATIVE_MODE_TAB,
				Identifier.fromNamespaceAndPath("bettermod", "item_group"),
				ITEM_GROUP
		);

		if (Blocks.BOOKSHELF instanceof BetterBookshelfBlock betterBookshelfBlock) {
			LOGGER.info("BetterBookshelves was successful!");
			BOOKSHELF_BLOCK_ENTITY_TYPE = registerBlockEntityType(
					BetterBookshelfBlockEntity.ID,
					betterBookshelfBlock,
					BetterBookshelfBlockEntity::new
			);
		} else {
			LOGGER.error("BetterBookshelves was not successful! This is a bug!");
		}
		var room_id = Identifier.fromNamespaceAndPath("bettermod", "room_controller");
		ROOM_CONTROLLER_BLOCK = registerBlock(
				BlockItemId.create(room_id, room_id),
				Block::new,
				Blocks.GLASS
		);

		SLING_MECHANISM = registerItem(Identifier.fromNamespaceAndPath("bettermod", "sling_mechanism"), Item::new);

		BIT_HOPPER_BLOCK = registerBlock(BitHopperBlock.BlockItemID, BitHopperBlock::new, Blocks.HOPPER);
		PULL_HOPPER_BLOCK = registerBlock(PullHopperBlock.BlockItemID, PullHopperBlock::new, Blocks.HOPPER);
		STICK_HOPPER_BLOCK = registerBlock(StickHopperBlock.BlockItemID, StickHopperBlock::new, Blocks.HOPPER);

		BIT_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				BitHopperBlockEntity.ID,
				BIT_HOPPER_BLOCK,
				BitHopperBlockEntity::new
		);
		PULL_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				PullHopperBlockEntity.ID,
				PULL_HOPPER_BLOCK,
				PullHopperBlockEntity::new
		);
		STICK_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				StickHopperBlockEntity.ID,
				STICK_HOPPER_BLOCK,
				StickHopperBlockEntity::new
		);

		BOOKSHELF_SCREEN_HANDLER_TYPE = registerScreenHandler(
				BetterBookshelfScreenHandler.ID,
				BetterBookshelfScreenHandler::new
		);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		registerScreen(BOOKSHELF_SCREEN_HANDLER_TYPE);
	}
}
