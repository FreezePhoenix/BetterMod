package com.techteam.fabric.bettermod.impl;

import com.techteam.fabric.bettermod.api.block.BetterBlock;
import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IClientLoadableBlockEntity;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IServerLoadableBlockEntity;
import com.techteam.fabric.bettermod.impl.block.*;
import com.techteam.fabric.bettermod.impl.block.entity.*;
import com.techteam.fabric.bettermod.impl.client.gui.BetterBookshelfScreenHandler;
import com.techteam.fabric.bettermod.impl.client.gui.BetterScreen;
import com.techteam.fabric.bettermod.impl.client.gui.HopperScreenHandler;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;


public class BetterMod implements ModInitializer, ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static BetterModConfig CONFIG;
	public static BlockEntityType<BetterBookshelfBlockEntity> BOOKSHELF_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<BetterBookshelfScreenHandler> BOOKSHELF_SCREEN_HANDLER_TYPE;
	public static Block ROOM_CONTROLLER_BLOCK;
	public static BetterBlock<BitHopperBlockEntity> BIT_HOPPER_BLOCK;
	public static BlockEntityType<BitHopperBlockEntity> BIT_HOPPER_BLOCK_ENTITY_TYPE;
	public static BetterBlock<PullHopperBlockEntity> PULL_HOPPER_BLOCK;
	public static BlockEntityType<PullHopperBlockEntity> PULL_HOPPER_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<HopperScreenHandler> HOPPER_SCREEN_HANDLER_TYPE;
	public static BetterBlock<StickHopperBlockEntity> STICK_HOPPER_BLOCK;
	public static BlockEntityType<StickHopperBlockEntity> STICK_HOPPER_BLOCK_ENTITY_TYPE;
	public static Collection<ItemStack> ITEMS = new ArrayList<>();

	public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
	                                                          .icon(() -> ROOM_CONTROLLER_BLOCK.asItem()
	                                                                                           .getDefaultStack())
	                                                          .displayName(Text.translatable("bettermod.item_group"))
	                                                          .entries((context, entries) -> entries.addAll(ITEMS))
	                                                          .build();

	public static <E extends BetterBlockEntity> BetterBlock<E> registerBlock(Identifier ID, BetterBlock<E> block) {
		Registry.register(Registries.BLOCK, ID, block);
		final BlockItem blockItem = Registry.register(Registries.ITEM, ID, new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, ID))));
		ITEMS.add(blockItem.getDefaultStack());
		return block;
	}

	public static Block registerBlock(Identifier ID, Block block) {
		Registry.register(Registries.BLOCK, ID, block);
		final BlockItem blockItem = Registry.register(Registries.ITEM, ID, new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, ID))));
		ITEMS.add(blockItem.getDefaultStack());
		return block;
	}

	public static <E extends BetterBlockEntity> BlockEntityType<E> registerBlockEntityType(Identifier ID, BetterBlock<E> block) {
		return Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				ID,
				FabricBlockEntityTypeBuilder.create(block::createBlockEntity, block).build()
		);
	}

	public static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(Identifier ID, ScreenHandlerType.Factory<T> factory) {
		return Registry.register(
				Registries.SCREEN_HANDLER,
				ID,
				new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES)
		);
	}

	@Environment(EnvType.CLIENT)
	private static <T extends SyncedGuiDescription> void registerScreen(ScreenHandlerType<T> screenHandlerType) {
		HandledScreens.register(screenHandlerType, BetterScreen<T>::new);
	}

	@Override
	public void onInitialize() {
		AutoConfig.register(BetterModConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(BetterModConfig.class).getConfig();

		Registry.register(Registries.ITEM_GROUP, Identifier.of("bettermod", "item_group"), ITEM_GROUP);

		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(IServerLoadableBlockEntity::onLoad);
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(IServerLoadableBlockEntity::onUnload);

		if (Blocks.BOOKSHELF instanceof BetterBookshelfBlock betterBookshelfBlock) {
			LOGGER.info("BetterBookshelves was successful!");
			BOOKSHELF_BLOCK_ENTITY_TYPE = registerBlockEntityType(BetterBookshelfBlock.ID, betterBookshelfBlock);
		} else {
			LOGGER.error("BetterBookshelves was not successful! This is a bug!");
		}

		ROOM_CONTROLLER_BLOCK = registerBlock(
				RoomControllerBlock.ID,
				new RoomControllerBlock(AbstractBlock.Settings.copy(Blocks.GLASS).registryKey( RegistryKey.of(RegistryKeys.BLOCK,RoomControllerBlock.ID)))
		);
		BIT_HOPPER_BLOCK = registerBlock(
				BitHopperBlock.ID,
				new BitHopperBlock(AbstractBlock.Settings.copy(Blocks.HOPPER).registryKey( RegistryKey.of(RegistryKeys.BLOCK,BitHopperBlock.ID)))
		);
		STICK_HOPPER_BLOCK = registerBlock(
				StickHopperBlock.ID,
				new StickHopperBlock(AbstractBlock.Settings.copy(Blocks.HOPPER).registryKey( RegistryKey.of(RegistryKeys.BLOCK,StickHopperBlock.ID)))
		);
		PULL_HOPPER_BLOCK = registerBlock(
				PullHopperBlock.ID,
				new PullHopperBlock(AbstractBlock.Settings.copy(Blocks.HOPPER).registryKey( RegistryKey.of(RegistryKeys.BLOCK,PullHopperBlock.ID)))
		);

		BIT_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(BitHopperBlockEntity.ID, BIT_HOPPER_BLOCK);
		STICK_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(StickHopperBlockEntity.ID, STICK_HOPPER_BLOCK);
		PULL_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(PullHopperBlockEntity.ID, PULL_HOPPER_BLOCK);

		BOOKSHELF_SCREEN_HANDLER_TYPE = registerScreenHandler(
				BetterBookshelfBlock.ID,
				BetterBookshelfScreenHandler::new
		);
		HOPPER_SCREEN_HANDLER_TYPE = registerScreenHandler(PullHopperBlock.ID, HopperScreenHandler::new);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register(IClientLoadableBlockEntity::onLoad);
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(IClientLoadableBlockEntity::onUnload);

		registerScreen(BOOKSHELF_SCREEN_HANDLER_TYPE);
		registerScreen(HOPPER_SCREEN_HANDLER_TYPE);
	}
}
