package com.techteam.fabric.bettermod.impl.mixin.client;

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.techteam.fabric.bettermod.impl.hooks.RenderHooks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.WorldRenderer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Iterator;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
	@ModifyExpressionValue(slice = @Slice(from = @At(value = "INVOKE_STRING",
	                                                 target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
	                                                 args = "ldc=blockentities"),
	                                      to = @At(value = "INVOKE_STRING",
	                                               target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
	                                               args = "ldc=destroyProgress")),
	                       at = @At(ordinal = 0,
	                                value = "INVOKE",
	                                target = "Ljava/util/List;iterator()Ljava/util/Iterator;"),
	                       method = "render")
	private @NotNull Iterator<BlockEntity> iteratorIntercept(final @NotNull Iterator<BlockEntity> iterator) {
		return Iterators.filter(iterator, RenderHooks::shouldRenderTileEntity);
	}
}
