package com.techteam.fabric.bettermod.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class BetterPerfModelLoadingPlugin implements ModelLoadingPlugin {
	private static final ModelIdentifier ROOM_CONTROLLER_BLOCK_MODEL_ID = new ModelIdentifier(Identifier.of("betterperf", "room_controller"), "");
	private static final ModelIdentifier ROOM_CONTROLLER_ITEM_MODEL_ID = new ModelIdentifier(Identifier.of("betterperf", "room_controller"), "inventory");
	private static final UnbakedModel ROOM_CONTROLLER_MODEL = new RoomControllerModel();

	@Override
	public void onInitializeModelLoader(@NotNull Context pluginContext) {
		// We want to add our model when the models are loaded
		pluginContext.modifyModelOnLoad().register((original, context) -> {
			var ID = context.topLevelId();
			// This is called for every model that is loaded, so make sure we only target ours
			if(ID != null && (ID.equals(ROOM_CONTROLLER_BLOCK_MODEL_ID) || ID.equals(ROOM_CONTROLLER_ITEM_MODEL_ID))) {
				return ROOM_CONTROLLER_MODEL;
			} else {
				// If we don't modify the model we just return the original as-is
				return original;
			}
		});
	}
}
