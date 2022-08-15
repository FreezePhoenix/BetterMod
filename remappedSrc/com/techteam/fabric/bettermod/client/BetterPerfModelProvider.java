package com.techteam.fabric.bettermod.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class BetterPerfModelProvider implements ModelResourceProvider {
	private static final Identifier ROOM_CONTROLLER_BLOCK_MODEL_ID = new Identifier("betterperf:block/room_controller");
	private static final Identifier ROOM_CONTROLLER_ITEM_MODEL_ID = new Identifier("betterperf:item/room_controller");
	private static final UnbakedModel ROOM_CONTROLLER_MODEL = new RoomControllerModel();

	public BetterPerfModelProvider(ResourceManager rm) {
	}

	@Override
	public @Nullable UnbakedModel loadModelResource(@NotNull Identifier identifier, ModelProviderContext modelProviderContext) {
        return identifier.equals(ROOM_CONTROLLER_BLOCK_MODEL_ID) || identifier.equals(ROOM_CONTROLLER_ITEM_MODEL_ID)
               ? ROOM_CONTROLLER_MODEL
               : null;
	}
}
