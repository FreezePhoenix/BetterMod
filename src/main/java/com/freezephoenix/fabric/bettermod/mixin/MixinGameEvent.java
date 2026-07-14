package com.freezephoenix.fabric.bettermod.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.util.Util;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashSet;
import java.util.Set;

@Mixin(GameEvent.class)
public class MixinGameEvent {
	public static class Vibrations {

		private static final Set<String> BETTERMOD$VIBRATIONS = Util.make(new HashSet<>(), (set) -> {
			set.add("flap");
			set.add("resonate_1");
			set.add("resonate_2");
			set.add("resonate_3");
			set.add("resonate_4");
			set.add("resonate_5");
			set.add("resonate_6");
			set.add("resonate_7");
			set.add("resonate_8");
			set.add("resonate_9");
			set.add("resonate_10");
			set.add("resonate_11");
			set.add("resonate_12");
			set.add("resonate_13");
			set.add("resonate_14");
			set.add("resonate_15");
			set.add("block_attach");
			set.add("block_change");
			set.add("block_close");
			set.add("block_destroy");
			set.add("block_detach");
			set.add("block_open");
			set.add("block_place");
			set.add("block_activate");
			set.add("block_deactivate");
			set.add("bounce");
			set.add("container_close");
			set.add("container_open");
			set.add("drink");
			set.add("eat");
			set.add("elytra_glide");
			set.add("entity_damage");
			set.add("entity_die");
			set.add("entity_dismount");
			set.add("entity_interact");
			set.add("entity_mount");
			set.add("entity_place");
			set.add("entity_action");
			set.add("equip");
			set.add("explode");
			set.add("fluid_pickup");
			set.add("fluid_place");
			set.add("hit_ground");
			set.add("instrument_play");
			set.add("item_interact_finish");
			set.add("lightning_strike");
			set.add("note_block_play");
			set.add("prime_fuse");
			set.add("projectile_land");
			set.add("projectile_shoot");
			set.add("shear");
			set.add("splash");
			set.add("step");
			set.add("swim");
			set.add("teleport");
			set.add("unequip");
		});
	}
	@WrapMethod(method="Lnet/minecraft/world/level/gameevent/GameEvent;register(Ljava/lang/String;I)Lnet/minecraft/core/Holder$Reference;")
	private static Holder.Reference<GameEvent> wrapRegister(String name, int range, Operation<Holder.Reference<GameEvent>> original) {
		if(Vibrations.BETTERMOD$VIBRATIONS.contains(name)) {
			range = 32;
		}
		return original.call(name, range);
	}
}
