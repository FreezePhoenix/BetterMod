package com.techteam.fabric.bettermod.impl.mixin;

import com.techteam.fabric.bettermod.api.hooks.IRoomCaching;
import com.techteam.fabric.bettermod.impl.client.RoomTracker;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity implements IRoomCaching {
	@Shadow
	@Final
	private List<ClientPlayerTickable> tickables;

	@Inject(at = @At(value = "TAIL"),
	        method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)V")
	private void addTickHooks(CallbackInfo info) {
		tickables.add(RoomTracker.bind(this));
	}
}
