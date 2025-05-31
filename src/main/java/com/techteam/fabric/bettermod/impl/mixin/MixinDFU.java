package com.techteam.fabric.bettermod.impl.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.techteam.fabric.bettermod.impl.block.entity.*;
import net.minecraft.datafixer.schema.Schema1460;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema1460.class)
public abstract class MixinDFU extends Schema {
	public MixinDFU(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Shadow
	protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
		throw new UnsupportedOperationException();
	}

	@ModifyReturnValue(method = "registerBlockEntities",
	                   at = @At("RETURN"))
	private Map<String, Supplier<TypeTemplate>> onRegisterBlockEntities(Map<String, Supplier<TypeTemplate>> map, Schema schema) {
		registerInventory(schema, map, "bettermod:bit_hopper");
		registerInventory(schema, map, "bettermod:pull_hopper");
		registerInventory(schema, map, "bettermod:stick_hopper");
		registerInventory(schema, map, "minecraft:bookshelf");
		return map;
	}
}
