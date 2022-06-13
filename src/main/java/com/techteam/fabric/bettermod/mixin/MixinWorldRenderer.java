package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.hooks.RenderHooks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
	@Redirect(slice = @Slice(from = @At(value = "INVOKE_STRING",
										target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
										args = "ldc=blockentities"),
							 to = @At(value = "INVOKE_STRING",
									  target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
									  args = "ldc=destroyProgress")),
			  at = @At(ordinal = 0,
					   value = "INVOKE",
					   target = "Ljava/util/List;iterator()Ljava/util/Iterator;"),
			  method = "render")
	private @NotNull Iterator<BlockEntity> iteratorIntercept(final @NotNull List<BlockEntity> list) {
		return list.listIterator();
	}

	@Redirect(slice = @Slice(from = @At(value = "INVOKE_STRING",
										target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
										args = "ldc=blockentities"),
							 to = @At(value = "INVOKE_STRING",
									  target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
									  args = "ldc=destroyProgress")),
			  at = @At(ordinal = 1,
					   value = "INVOKE",
					   target = "Ljava/util/Iterator;hasNext()Z"),
			  method = "render")
	private boolean nextHook(final @NotNull Iterator<BlockEntity> iterator) {
		if (iterator instanceof ListIterator<BlockEntity> listIterator) {
			Profiler profiler = MinecraftClient.getInstance()
											   .getProfiler();
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
