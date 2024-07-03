package com.techteam.fabric.bettermod.impl.mixin;;
import com.techteam.fabric.bettermod.BetterMod;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(World.class)
public abstract class MixinWorld {
    @Inject(method = "tickBlockEntities",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/chunk/BlockEntityTickInvoker;tick()V",
                     shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void afterTickBlockEntity(CallbackInfo ci, @NotNull Profiler profiler) {
        profiler.pop();
    }

    @Inject(method = "tickBlockEntities",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/chunk/BlockEntityTickInvoker;tick()V",
                     shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void beforeTickBlockEntity(CallbackInfo ci, @NotNull Profiler profiler, Iterator<?> iterator, boolean bl, @NotNull BlockEntityTickInvoker blockEntityTickInvoker) {
        profiler.push(blockEntityTickInvoker.getName());
    }
}
