package com.techteam.fabric.bettermod.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class BetterPerfModelLoadingPlugin implements ModelLoadingPlugin {
	private static final ModelIdentifier ROOM_CONTROLLER_BLOCK_MODEL_ID = new ModelIdentifier(Identifier.of("betterperf",
	                                                                                                        "room_controller"
	), "");

	@Override
	public void onInitializeModelLoader(@NotNull Context pluginContext) {
		// We want to add our model when the models are loaded
		pluginContext.modifyModelAfterBake().register((original, context) -> {
			var ID = context.topLevelId();
			// This is called for every model that is loaded, so make sure we only target ours
			// If we don't modify the model we just return the original as-is
			return ROOM_CONTROLLER_BLOCK_MODEL_ID.equals(ID)
			       ? new RoomControllerModel(original)
			       : original;
		});
	}
}
