package com.freezephoenix.fabric.bettermod;

import com.freezephoenix.fabric.FreezeLib;
import com.freezephoenix.fabric.bettermod.block.*;
import com.freezephoenix.fabric.bettermod.block.entity.*;
import com.freezephoenix.fabric.bettermod.gui.BetterBookshelfScreenHandler;
import com.freezephoenix.fabric.gui.BetterScreen;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BetterMod implements ModInitializer, ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
	public static Block ROOM_CONTROLLER_BLOCK;

	public static FreezeLib.BetterBlockEntity<ResonantSculkSensorBlock, ResonantSculkSensorBlockEntity> RESONANT_SCULK_SENSOR;
	public static FreezeLib.BetterBlockEntity<BitHopperBlock, BitHopperBlockEntity> BIT_HOPPER;
	public static FreezeLib.BetterBlockEntity<PullHopperBlock, PullHopperBlockEntity> PULL_HOPPER;
	public static FreezeLib.BetterBlockEntity<StickHopperBlock, StickHopperBlockEntity> STICK_HOPPER;
	public static FreezeLib.BetterBlockEntity<BetterBookshelfBlock, BetterBookshelfBlockEntity> BOOKSHELF;
	public static MenuType<BetterBookshelfScreenHandler> BOOKSHELF_SCREEN_HANDLER_TYPE;

	public static final FreezeLib.FreezeTab ITEM_GROUP = FreezeLib.registerCreativeTab("bettermod",() -> ROOM_CONTROLLER_BLOCK);

	public static Item SLING_MECHANISM;

	public static <T extends AbstractContainerMenu> MenuType<T> registerScreenHandler(Identifier ID, MenuType.MenuSupplier<T> factory) {
		return Registry.register(BuiltInRegistries.MENU, ID, new MenuType<>(factory, FeatureFlagSet.of()));
	}

	@Environment(EnvType.CLIENT)
	private static <T extends SyncedGuiDescription> void registerScreen(MenuType<T> screenHandlerType) {
		// Technically we could just do this:
		// MenuScreens.register(screenHandlerType, CottonInventoryScreen<T>::new);
		MenuScreens.register(screenHandlerType, BetterScreen<T>::new);
	}

	@Override
	public void onInitialize() {

		if (Blocks.BOOKSHELF instanceof BetterBookshelfBlock betterBookshelfBlock) {
			LOGGER.info("BetterBookshelves was successful!");
			BOOKSHELF = FreezeLib.registerBlockEntity(betterBookshelfBlock, BetterBookshelfBlockEntity.ID, BetterBookshelfBlockEntity::new);
		} else {
			LOGGER.error("BetterBookshelves was not successful! This is a bug!");
		}
		var room_id = Identifier.fromNamespaceAndPath("bettermod", "room_controller");
		ROOM_CONTROLLER_BLOCK = FreezeLib.registerBlock(
				BlockItemId.create(room_id, room_id),
				Block::new,
				Blocks.GLASS
		);

		SLING_MECHANISM = FreezeLib.registerItem(Identifier.fromNamespaceAndPath("bettermod", "sling_mechanism"), Item::new);

		BIT_HOPPER				= FreezeLib.registerBlockEntity(BitHopperBlock.BlockItemID,BitHopperBlock::new,Blocks.HOPPER, BitHopperBlockEntity.ID, BitHopperBlockEntity::new);
		PULL_HOPPER				= FreezeLib.registerBlockEntity(PullHopperBlock.BlockItemID,PullHopperBlock::new,Blocks.HOPPER, PullHopperBlockEntity.ID, PullHopperBlockEntity::new);
		STICK_HOPPER			= FreezeLib.registerBlockEntity(StickHopperBlock.BlockItemID,StickHopperBlock::new,Blocks.HOPPER, StickHopperBlockEntity.ID, StickHopperBlockEntity::new);
		RESONANT_SCULK_SENSOR	= FreezeLib.registerBlockEntity(ResonantSculkSensorBlock.BlockItemID,	ResonantSculkSensorBlock::new,	Blocks.SCULK_SENSOR, ResonantSculkSensorBlockEntity.ID, ResonantSculkSensorBlockEntity::new);

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
