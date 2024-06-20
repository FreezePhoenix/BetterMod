package com.techteam.fabric.bettermod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.techteam.fabric.bettermod.block.entity.*;
import net.minecraft.datafixer.schema.Schema1460;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema1460.class)
public abstract class MixinDFU extends Schema {
	public MixinDFU(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Shadow
	protected static void method_5273(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
	}

	@Inject(method="registerBlockEntities", at = @At("RETURN"))
	private void onRegisterBlockEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
		var map = cir.getReturnValue();
		method_5273(schema, map, BitHopperBlockEntity.ID.toString());
		method_5273(schema, map, PullHopperBlockEntity.ID.toString());
		method_5273(schema, map, StickHopperBlockEntity.ID.toString());
		method_5273(schema, map, RoomControllerBlockEntity.ID.toString());
		method_5273(schema, map, "minecraft:bookshelf");
	}
}
