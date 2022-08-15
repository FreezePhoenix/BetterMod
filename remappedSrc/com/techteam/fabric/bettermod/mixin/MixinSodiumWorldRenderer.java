package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.hooks.RenderHooks;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.ListIterator;

@Mixin(SodiumWorldRenderer.class)
public abstract class MixinSodiumWorldRenderer {
    @Redirect(at = @At(value = "INVOKE",
                       target = "Ljava/util/Iterator;hasNext()Z"),
              method = "renderTileEntities")
    private boolean hasNextHook(final @NotNull Iterator<BlockEntity> iterator) {
        if (iterator instanceof ListIterator<BlockEntity> listIterator) {
            Profiler profiler = MinecraftClient.getInstance().getProfiler();
            profiler.push("better_culling");
            while (listIterator.hasNext()) {
                if (RenderHooks.shouldRenderTileEntity(listIterator.next())) {
                    listIterator.previous();
                    profiler.pop();
                    return true;
                }
            }
            profiler.pop();
            return false;
        }
        return iterator.hasNext();
    }
}