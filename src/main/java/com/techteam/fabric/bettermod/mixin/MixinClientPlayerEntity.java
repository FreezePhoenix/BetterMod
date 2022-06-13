package com.techteam.fabric.bettermod.mixin;

import com.mojang.authlib.GameProfile;
import com.techteam.fabric.bettermod.client.RoomTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.entity.EntityLike;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sun.misc.Unsafe;

import java.lang.annotation.Inherited;
import java.util.List;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    private List<ClientPlayerTickable> tickables;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Inject(at = @At(value = "TAIL"),
            method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/ClientPlayNetworkHandler;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)V")
    private void addTickHook(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfo info) {
        tickables.add(RoomTracker.bind(this));
    }
}
