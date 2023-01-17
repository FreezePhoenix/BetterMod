package com.techteam.fabric.bettermod.mixin;

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.techteam.fabric.bettermod.hooks.RenderHooks;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

@Mixin(SodiumWorldRenderer.class)
public abstract class MixinSodiumWorldRenderer {
    @Shadow private ClientWorld world;

    @ModifyExpressionValue(at = @At(value = "INVOKE",
                                    target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;"),
                           method = "renderTileEntities")
    private Iterator<BlockEntity> hasNextHook(final @NotNull Iterator<BlockEntity> iterator) {
        final Profiler profiler = this.world.getProfiler();
        return new Iterator<>() {
            private BlockEntity next = null;
            @Override
            public boolean hasNext() {
                if(next != null) {
                    return true;
                }
                profiler.push("better_culling");
                while (iterator.hasNext()) {
                    BlockEntity possibleNext = iterator.next();
                    if (RenderHooks.shouldRenderTileEntity(possibleNext)) {
                        profiler.pop();
                        next = possibleNext;
                        return true;
                    }
                }
                profiler.pop();
                return false;
            }

            @Override
            public BlockEntity next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                BlockEntity toReturn = next;
                next = null;
                return toReturn;
            }
        };
    }
}