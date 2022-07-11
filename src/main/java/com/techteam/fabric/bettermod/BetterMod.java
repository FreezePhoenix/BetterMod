package com.techteam.fabric.bettermod;

import com.techteam.fabric.bettermod.block.BetterBlock;
import com.techteam.fabric.bettermod.block.BetterBookshelfBlock;
import com.techteam.fabric.bettermod.block.BitHopperBlock;
import com.techteam.fabric.bettermod.block.RoomControllerBlock;
import com.techteam.fabric.bettermod.block.entity.BetterBookshelfBlockEntity;
import com.techteam.fabric.bettermod.block.entity.BitHopperBlockEntity;
import com.techteam.fabric.bettermod.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.block.entity.loadable.IClientLoadableBlockEntity;
import com.techteam.fabric.bettermod.block.entity.loadable.IServerLoadableBlockEntity;
import com.techteam.fabric.bettermod.client.BetterPerfModelProvider;
import com.techteam.fabric.bettermod.client.RoomControllerEntityRenderer;
import com.techteam.fabric.bettermod.client.gui.*;
import com.techteam.fabric.bettermod.network.NetworkHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Shader;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Unsafe;


public class BetterMod implements ModInitializer, ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static BlockEntityType<BetterBookshelfBlockEntity> BOOKSHELF_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<BetterBookshelfScreenHandler> BOOKSHELF_SCREEN_HANDLER_TYPE;
	public static BetterBlock<RoomControllerBlockEntity> ROOM_CONTROLLER_BLOCK;
	public static BlockEntityType<RoomControllerBlockEntity> ROOM_CONTROLLER_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<RoomControllerScreenHandler> ROOM_CONTROLLER_SCREEN_HANDLER_TYPE;
	public static BetterBlock<BitHopperBlockEntity> BIT_HOPPER_BLOCK;
	public static BlockEntityType<BitHopperBlockEntity> BIT_HOPPER_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<BitHopperScreenHandler> BIT_HOPPER_SCREEN_HANDLER_TYPE;

	@Override
	public void onInitialize() {
		if (Blocks.BOOKSHELF instanceof BetterBookshelfBlock) {
			LOGGER.info("BetterBookshelves was successful!");
		} else {
			LOGGER.error("BetterBookshelves was not successful! This is a bug!");
		}
		BOOKSHELF_BLOCK_ENTITY_TYPE = Registry.register(
				Registry.BLOCK_ENTITY_TYPE,
				BetterBookshelfBlock.ID,
				FabricBlockEntityTypeBuilder.create(BetterBookshelfBlockEntity::new, Blocks.BOOKSHELF).build(null)
		);
		BOOKSHELF_SCREEN_HANDLER_TYPE = Registry.register(
				Registry.SCREEN_HANDLER,
				BetterBookshelfBlock.ID,
				new ExtendedScreenHandlerType<>(BetterBookshelfScreenHandler::new)
		);
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof IServerLoadableBlockEntity loadableBlockEntity) {
				loadableBlockEntity.onServerLoad();
			}
		});
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof IServerLoadableBlockEntity loadableBlockEntity) {
				loadableBlockEntity.onServerUnload();
			}
		});
		NetworkHandlers.initServerHandlers();
		ROOM_CONTROLLER_BLOCK = Registry.register(
				Registry.BLOCK,
				RoomControllerBlock.ID,
				new RoomControllerBlock(FabricBlockSettings.of(Material.GLASS))
		);
		ROOM_CONTROLLER_BLOCK_ENTITY_TYPE = Registry.register(
				Registry.BLOCK_ENTITY_TYPE,
				RoomControllerBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(RoomControllerBlockEntity::new, ROOM_CONTROLLER_BLOCK).build()
		);
		ROOM_CONTROLLER_SCREEN_HANDLER_TYPE = Registry.register(
				Registry.SCREEN_HANDLER,
				RoomControllerBlock.ID,
				new ExtendedScreenHandlerType<>(RoomControllerScreenHandler::new)
		);
		Registry.register(
				Registry.ITEM,
				RoomControllerBlock.ID,
				new BlockItem(ROOM_CONTROLLER_BLOCK, new Item.Settings().group(ItemGroup.MISC))
		);
		BIT_HOPPER_BLOCK = Registry.register(
				Registry.BLOCK,
				BitHopperBlock.ID,
				new BitHopperBlock(FabricBlockSettings.of(Material.METAL, MapColor.STONE_GRAY).requiresTool().strength(3.0f, 4.8f).sounds(
						BlockSoundGroup.METAL).nonOpaque())
		);
		BIT_HOPPER_BLOCK_ENTITY_TYPE = Registry.register(
				Registry.BLOCK_ENTITY_TYPE,
				BitHopperBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(BitHopperBlockEntity::new, BIT_HOPPER_BLOCK).build()
		);
		BIT_HOPPER_SCREEN_HANDLER_TYPE = Registry.register(
				Registry.SCREEN_HANDLER,
				BitHopperBlock.ID,
				new ExtendedScreenHandlerType<>(BitHopperScreenHandler::new)
		);
		Registry.register(
				Registry.ITEM,
				BitHopperBlock.ID,
				new BlockItem(BIT_HOPPER_BLOCK, new Item.Settings().group(ItemGroup.MISC))
		);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register(ROOM_CONTROLLER_BLOCK_ENTITY_TYPE, RoomControllerEntityRenderer::new);
		BlockRenderLayerMap.INSTANCE.putBlock(ROOM_CONTROLLER_BLOCK, RenderLayer.getCutoutMipped());
		HandledScreens.register(ROOM_CONTROLLER_SCREEN_HANDLER_TYPE, RoomControllerScreen::new);
		HandledScreens.register(BOOKSHELF_SCREEN_HANDLER_TYPE, BetterBookshelfScreen::new);
		HandledScreens.register(BIT_HOPPER_SCREEN_HANDLER_TYPE, BitHopperScreen::new);
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(BetterPerfModelProvider::new);
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof IClientLoadableBlockEntity loadableBlockEntity) {
				loadableBlockEntity.onClientLoad();
			}
		});
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof IClientLoadableBlockEntity loadableBlockEntity) {
				loadableBlockEntity.onClientUnload();
			}
		});
	}
}
