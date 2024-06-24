package com.techteam.fabric.bettermod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.techteam.fabric.bettermod.hooks.RenderHooks;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

// TODO: FIX
@Mixin(SodiumWorldRenderer.class)
public abstract class MixinSodiumWorldRenderer {
    @Shadow private ClientWorld world;

    @WrapWithCondition(
        at = @At(
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/SodiumWorldRenderer;renderBlockEntity(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/BufferBuilderStorage;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDDLnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;Lnet/minecraft/block/entity/BlockEntity;)V",
            remap = false
        ),
        method = "renderBlockEntities(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/BufferBuilderStorage;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDDLnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;)V"
    )
    private boolean shouldRender(MatrixStack bufferBuilder, BufferBuilderStorage entry, Long2ObjectMap<SortedSet<BlockBreakingInfo>> transformer, float stage, VertexConsumerProvider.Immediate matrices, double bufferBuilders, double blockBreakingProgressions, double tickDelta, BlockEntityRenderDispatcher immediate, BlockEntity blockEntity) {
        final Profiler profiler = this.world.getProfiler();
        profiler.push("better_culling");
        boolean result = RenderHooks.shouldRenderTileEntity(blockEntity);
        profiler.pop();
        return result;
    }
}