package com.techteam.fabric.bettermod;

import com.techteam.fabric.bettermod.api.block.BetterBlock;
import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.block.*;
import com.techteam.fabric.bettermod.block.entity.*;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IClientLoadableBlockEntity;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IServerLoadableBlockEntity;
import com.techteam.fabric.bettermod.client.BetterPerfModelLoadingPlugin;
import com.techteam.fabric.bettermod.client.RoomControllerEntityRenderer;
import com.techteam.fabric.bettermod.client.gui.BetterBookshelfScreenHandler;
import com.techteam.fabric.bettermod.client.gui.BetterScreen;
import com.techteam.fabric.bettermod.client.gui.HopperScreenHandler;
import com.techteam.fabric.bettermod.client.gui.RoomControllerScreenHandler;
import com.techteam.fabric.bettermod.network.BoxUpdatePayload;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.netty.buffer.ByteBuf;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collection;
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
	public static BetterBlock<PullHopperBlockEntity> PULL_HOPPER_BLOCK;
	public static BlockEntityType<PullHopperBlockEntity> PULL_HOPPER_BLOCK_ENTITY_TYPE;
	public static ScreenHandlerType<HopperScreenHandler> HOPPER_SCREEN_HANDLER_TYPE;
	public static BetterBlock<StickHopperBlockEntity> STICK_HOPPER_BLOCK;
	public static BlockEntityType<StickHopperBlockEntity> STICK_HOPPER_BLOCK_ENTITY_TYPE;

	public static Collection<ItemStack> ITEMS = new ArrayList<>();

	private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
	                                                           .icon(() -> new ItemStack(ROOM_CONTROLLER_BLOCK.asItem()))
	                                                           .displayName(Text.translatable("bettermod.item_group"))
	                                                           .entries((context, entries) -> entries.addAll(ITEMS))
	                                                           .build();

	@Contract("_, _ -> param2")
	public static <E extends BetterBlockEntity> BetterBlock<E> registerBlock(Identifier ID, BetterBlock<E> block) {
		Registry.register(Registries.BLOCK, ID, block);
		final BlockItem blockItem = Registry.register(
				Registries.ITEM,
				ID,
				new BlockItem(block, new Item.Settings())
		);
		ITEMS.add(blockItem.getDefaultStack());
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

	public static PacketCodec<RegistryByteBuf, BoxUpdatePayload.Vec3b> VEC3B = PacketCodec.tuple(
			PacketCodecs.BYTE,
			BoxUpdatePayload.Vec3b::x,
			PacketCodecs.BYTE,
			BoxUpdatePayload.Vec3b::y,
			PacketCodecs.BYTE,
			BoxUpdatePayload.Vec3b::z,
			BoxUpdatePayload.Vec3b::new
	);
	public static final PacketCodec<ByteBuf, BlockState> BLOCK_STATE = PacketCodecs.entryOf(Block.STATE_IDS);
	public static final PacketCodec<ByteBuf, BlockPos> BLOCK_POS = BlockPos.PACKET_CODEC;

	@Contract("_, _, _ -> !null")
	public static <T extends ScreenHandler, D> ScreenHandlerType<T> registerExtendedScreenHandler(Identifier ID, ExtendedScreenHandlerType.ExtendedFactory<T, D> factory, PacketCodec<? super RegistryByteBuf, D> codec) {
		return Registry.register(Registries.SCREEN_HANDLER, ID, new ExtendedScreenHandlerType<>(factory, codec));
	}

	@Contract("_, _ -> !null")
	public static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(Identifier ID, ScreenHandlerType.Factory<T> factory) {
		return Registry.register(Registries.SCREEN_HANDLER, ID, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
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
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(IServerLoadableBlockEntity::onUnLoad);

		BoxUpdatePayload.register();

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
				new RoomControllerBlock(AbstractBlock.Settings.copy(Blocks.GLASS).dynamicBounds())
		);

		ROOM_CONTROLLER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				RoomControllerBlock.ID,
				ROOM_CONTROLLER_BLOCK
		);
		ROOM_CONTROLLER_SCREEN_HANDLER_TYPE = registerExtendedScreenHandler(
				RoomControllerBlock.ID,
				RoomControllerScreenHandler::new,
				BLOCK_POS
		);
		BIT_HOPPER_BLOCK = registerBlock(
				BitHopperBlock.ID,
				new BitHopperBlock(AbstractBlock.Settings.copy(Blocks.HOPPER))
		);
		BIT_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				BitHopperBlockEntity.ID,
				BIT_HOPPER_BLOCK
		);
		PULL_HOPPER_BLOCK = registerBlock(
				PullHopperBlock.ID,
				new PullHopperBlock(AbstractBlock.Settings.copy(Blocks.HOPPER))
		);
		PULL_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				PullHopperBlockEntity.ID,
				PULL_HOPPER_BLOCK
		);
		HOPPER_SCREEN_HANDLER_TYPE = registerScreenHandler(
				PullHopperBlock.ID, HopperScreenHandler::new
		);
		STICK_HOPPER_BLOCK = registerBlock(
				StickHopperBlock.ID,
				new StickHopperBlock(AbstractBlock.Settings.copy(Blocks.HOPPER))
		);
		STICK_HOPPER_BLOCK_ENTITY_TYPE = registerBlockEntityType(
				StickHopperBlockEntity.ID,
				STICK_HOPPER_BLOCK
		);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if(view != null) {
				Object attachedData = view.getBlockEntityRenderData(pos);
				if (attachedData instanceof BlockState mimicState) {
					if (mimicState.getBlock() != BetterMod.ROOM_CONTROLLER_BLOCK) {
						var deferred_provider = ColorProviderRegistry.BLOCK.get(mimicState.getBlock());
						if (deferred_provider != null) {
							return deferred_provider.getColor(mimicState, view, pos, tintIndex);
						}
						return 0xFFFFFF;
					}
				}
			}
			return 0xFFFFFF;
		}, ROOM_CONTROLLER_BLOCK);

		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register(IClientLoadableBlockEntity::onLoad);
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(IClientLoadableBlockEntity::onUnload);
		BlockEntityRendererFactories.register(ROOM_CONTROLLER_BLOCK_ENTITY_TYPE, RoomControllerEntityRenderer::new);
		BlockRenderLayerMap.INSTANCE.putBlock(ROOM_CONTROLLER_BLOCK, RenderLayer.getCutoutMipped());
		registerScreen(BOOKSHELF_SCREEN_HANDLER_TYPE);
		registerScreen(HOPPER_SCREEN_HANDLER_TYPE);
		registerScreen(ROOM_CONTROLLER_SCREEN_HANDLER_TYPE);
		ModelLoadingPlugin.register(new BetterPerfModelLoadingPlugin());
	}
}
