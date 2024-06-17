package com.techteam.fabric.bettermod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.techteam.fabric.bettermod.hooks.RenderHooks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Iterator;
import java.util.NoSuchElementException;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
	@Shadow private ClientWorld world;

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
