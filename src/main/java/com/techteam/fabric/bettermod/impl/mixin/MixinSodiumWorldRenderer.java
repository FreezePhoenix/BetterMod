package com.techteam.fabric.bettermod.impl.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.techteam.fabric.bettermod.impl.hooks.RenderHooks;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.BlockBreakingInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.SortedSet;

@Mixin(SodiumWorldRenderer.class)
public abstract class MixinSodiumWorldRenderer {

	@WrapWithCondition(
			at = @At(
					value = "INVOKE",
					target = "Lme/jellysquid/mods/sodium/client/render/SodiumWorldRenderer;renderBlockEntity(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/BufferBuilderStorage;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDDLnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;Lnet/minecraft/block/entity/BlockEntity;)V",
					remap = false
			),
			method = "renderBlockEntities(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/BufferBuilderStorage;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDDLnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;)V"
	)
	private boolean shouldRender(MatrixStack matrices, BufferBuilderStorage bufferBuilders, Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions, float tickDelta, VertexConsumerProvider.Immediate immediate, double x, double y, double z, BlockEntityRenderDispatcher dispatcher, BlockEntity entity) {
		return RenderHooks.shouldRenderTileEntity(entity);
	}
}