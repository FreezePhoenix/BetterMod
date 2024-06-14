package com.techteam.fabric.bettermod;

import com.techteam.fabric.bettermod.block.*;
import com.techteam.fabric.bettermod.block.entity.*;
import com.techteam.fabric.bettermod.block.entity.loadable.IClientLoadableBlockEntity;
import com.techteam.fabric.bettermod.block.entity.loadable.IServerLoadableBlockEntity;
import com.techteam.fabric.bettermod.client.BetterPerfModelProvider;
import com.techteam.fabric.bettermod.client.RoomControllerEntityRenderer;
import com.techteam.fabric.bettermod.client.gui.*;
import com.techteam.fabric.bettermod.network.NetworkHandlers;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.util.Set;


public class BetterMod implements ModInitializer, ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static BetterModConfig CONFIG;
	public static BlockEntityType<BetterBookshelfBlockEntity> BOOKSHELF_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<BetterBookshelfScreenHandler> BOOKSHELF_SCREEN_HANDLER_TYPE;
	public static BetterBlock<RoomControllerBlockEntity> ROOM_CONTROLLER_BLOCK;
	public static BlockEntityType<RoomControllerBlockEntity> ROOM_CONTROLLER_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<RoomControllerScreenHandler> ROOM_CONTROLLER_SCREEN_HANDLER_TYPE;
	public static BetterBlock<BitHopperBlockEntity> BIT_HOPPER_BLOCK;
	public static BlockEntityType<BitHopperBlockEntity> BIT_HOPPER_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<BitHopperScreenHandler> BIT_HOPPER_SCREEN_HANDLER_TYPE;
	public static BetterBlock<PullHopperBlockEntity> PULL_HOPPER_BLOCK;
	public static BlockEntityType<PullHopperBlockEntity> PULL_HOPPER_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<PullHopperScreenHandler> PULL_HOPPER_SCREEN_HANDLER_TYPE;
	public static BetterBlock<StickHopperBlockEntity> STICK_HOPPER_BLOCK;
	public static BlockEntityType<StickHopperBlockEntity> STICK_HOPPER_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<StickHopperScreenHandler> STICK_HOPPER_SCREEN_HANDLER_TYPE;

	@Contract("_, _ -> param2")
	public static <E extends BetterBlockEntity> BetterBlock<E> registerBlock(Identifier ID, BetterBlock<E> block) {
		Registry.register(Registries.BLOCK, ID, block);
		final BlockItem blockItem = Registry.register(
				Registries.ITEM,
				ID,
				new BlockItem(block, new FabricItemSettings())
		);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.getDefaultTab()).register(content -> {
			content.add(blockItem);
		});
		return block;
	}

	public static <E extends BetterBlockEntity> BlockEntityType<E> registerBlockEntityType(Identifier ID, BetterBlock<E> block) {
		BlockEntityType<E> blockEntityType = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				ID,
				new BlockEntityType<>(block::createBlockEntity, Set.of(block), null)
		);
		ItemStorage.SIDED.registerForBlockEntity(
				(betterBlockEntity, direction) -> betterBlockEntity.SELF,
				blockEntityType
		);
		return blockEntityType;
	}

	@Contract("_, _ -> !null")
	public static <E extends ScreenHandler> ScreenHandlerType<E> registerScreenHandler(Identifier ID, ExtendedScreenHandlerType.ExtendedFactory<E> factory) {
		return Registry.register(Registries.SCREEN_HANDLER, ID, new ExtendedScreenHandlerType<>(factory));
	}

	@Environment(EnvType.CLIENT)
	private static <T extends SyncedGuiDescription> void registerScreen(ScreenHandlerType<T> screenHandlerType) {
		HandledScreens.register(screenHandlerType, BetterScreen<T>::new);
	}

	@Override
	public void onInitialize() {
		AutoConfig.register(BetterModConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(BetterModConfig.class).getConfig();

		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(IServerLoadableBlockEntity::onLoad);
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(IServerLoadableBlockEntity::onUnLoad);
		NetworkHandlers.initServerHandlers();
		if (Blocks.BOOKSHELF instanceof BetterBookshelfBlock betterBookshelfBlock) {
			LOGGER.info("BetterBookshelves was successful!");
			BOOKSHELF_BLOCK_ENTITY_TYPE = registerBlockEntityType(
					BetterBookshelfBlock.ID,
					betterBookshelfBlock
			);
		} else {
			LOGGER.error("BetterBookshelves was not successful! This is a bug!");
		}
		BOOKSHELF_SCREEN_HANDLER_TYPE = registerScreenHandler(
				BetterBookshelfBlock.ID,
				BetterBookshelfScreenHandler::new
		);
		ROOM_CONTROLLER_BLOCK = registerBlock(
				RoomControllerBlock.ID,
				new RoomControllerBlock(FabricBlockSettings.create(Material.GLASS))
		);
		ROOM_CONTROLLER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				RoomControllerBlock.ID,
				ROOM_CONTROLLER_BLOCK
		);
		ROOM_CONTROLLER_SCREEN_HANDLER_TYPE = registerScreenHandler(
				RoomControllerBlock.ID,
				RoomControllerScreenHandler::new
		);
		BIT_HOPPER_BLOCK = registerBlock(
				BitHopperBlock.ID,
				new BitHopperBlock(FabricBlockSettings.of(Material.METAL, MapColor.STONE_GRAY)
				                                      .requiresTool()
				                                      .strength(3.0f, 4.8f)
				                                      .sounds(BlockSoundGroup.METAL)
				                                      .nonOpaque())
		);
		BIT_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				BitHopperBlockEntity.ID,
				BIT_HOPPER_BLOCK
		);
		BIT_HOPPER_SCREEN_HANDLER_TYPE = registerScreenHandler(
				BitHopperBlock.ID, BitHopperScreenHandler::new
		);
		PULL_HOPPER_BLOCK = registerBlock(
				PullHopperBlock.ID,
				new PullHopperBlock(FabricBlockSettings.of(Material.METAL, MapColor.STONE_GRAY)
				                                       .requiresTool()
				                                       .strength(3.0f, 4.8f)
				                                       .sounds(BlockSoundGroup.METAL)
				                                       .nonOpaque())
		);
		PULL_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				PullHopperBlockEntity.ID,
				PULL_HOPPER_BLOCK
		);
		PULL_HOPPER_SCREEN_HANDLER_TYPE = registerScreenHandler(
				PullHopperBlock.ID, PullHopperScreenHandler::new
		);
		STICK_HOPPER_BLOCK = registerBlock(
				StickHopperBlock.ID,
				new StickHopperBlock(FabricBlockSettings.of(Material.METAL, MapColor.STONE_GRAY)
				                                        .requiresTool()
				                                        .strength(3.0f, 4.8f)
				                                        .sounds(BlockSoundGroup.METAL)
				                                        .nonOpaque())
		);
		STICK_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				StickHopperBlockEntity.ID,
				STICK_HOPPER_BLOCK
		);
		STICK_HOPPER_SCREEN_HANDLER_TYPE = registerScreenHandler(
				StickHopperBlock.ID, StickHopperScreenHandler::new
		);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register(IClientLoadableBlockEntity::onLoad);
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(IClientLoadableBlockEntity::onUnload);
		BlockEntityRendererRegistry.register(ROOM_CONTROLLER_BLOCK_ENTITY_TYPE, RoomControllerEntityRenderer::new);
		BlockRenderLayerMap.INSTANCE.putBlock(ROOM_CONTROLLER_BLOCK, RenderLayer.getCutoutMipped());
		registerScreen(BOOKSHELF_SCREEN_HANDLER_TYPE);
		registerScreen(BIT_HOPPER_SCREEN_HANDLER_TYPE);
		registerScreen(PULL_HOPPER_SCREEN_HANDLER_TYPE);
		registerScreen(STICK_HOPPER_SCREEN_HANDLER_TYPE);
		registerScreen(ROOM_CONTROLLER_SCREEN_HANDLER_TYPE);
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(BetterPerfModelProvider::new);
	}
}
