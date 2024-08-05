package com.techteam.fabric.bettermod.impl.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.techteam.fabric.bettermod.impl.hooks.RenderHooks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {
	@Shadow
	private World world;

	@WrapMethod(method = "render")
	private void wrapRenderForProfiling(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original) {
		var profiler = this.world.getProfiler();
		profiler.push(() -> EntityType.getId(entity.getType()).toString());
		original.call(entity, x, y, z, yaw, tickDelta, matrices, vertexConsumers, light);
		profiler.pop();
	}

	@ModifyReturnValue(method = "shouldRender",
	                   at = @At("RETURN"))
	private boolean shouldRenderHook(boolean returnValue, Entity entity) {
		return returnValue && RenderHooks.shouldRenderEntity(entity);
	}
}
