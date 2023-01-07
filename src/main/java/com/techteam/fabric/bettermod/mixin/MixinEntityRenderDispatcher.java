package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.hooks.RenderHooks;
import net.minecraft.block.entity.Hopper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {
    @Shadow private World world;

    @Inject(method = "render",
            at = @At("TAIL"))
    private <E extends Entity> void afterRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        this.world.getProfiler().pop();
    }

    @Inject(method = "render",
            at = @At("HEAD"))
    private <E extends Entity> void beforeRender(@NotNull E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        this.world.getProfiler().push(EntityType.getId(entity.getType()).toString());
    }

    @Inject(method = "shouldRender",
            at = @At("RETURN"),
            cancellable = true)
    private void shouldRenderHook(final Entity entity, final Frustum frustum, final double x, final double y, final double z, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            // if it's true, set our fallback value as the return
            cir.setReturnValue(RenderHooks.shouldRenderEntity(entity));
        }
    }
}
