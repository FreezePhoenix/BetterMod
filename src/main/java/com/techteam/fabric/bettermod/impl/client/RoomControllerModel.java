package com.techteam.fabric.bettermod.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class RoomControllerModel extends ForwardingBakedModel {
	private final BlockModels BLOCK_MODELS;

	public RoomControllerModel(BakedModel original) {
		wrapped = original;
		BLOCK_MODELS = MinecraftClient.getInstance().getBakedModelManager().getBlockModels();
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState blockState, BlockPos blockPos, Supplier<Random> randomSupplier, RenderContext renderContext) {
		Object attachedData = blockView.getBlockEntityRenderData(blockPos);
		if (attachedData instanceof BlockState mimicState) {
			if (mimicState.getRenderType() != BlockRenderType.INVISIBLE) {
				BLOCK_MODELS.getModel(mimicState).emitBlockQuads(blockView, mimicState, blockPos, randomSupplier, renderContext);
				return;
			}
		}
		super.emitBlockQuads(blockView, blockState, blockPos, randomSupplier, renderContext);
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
}
