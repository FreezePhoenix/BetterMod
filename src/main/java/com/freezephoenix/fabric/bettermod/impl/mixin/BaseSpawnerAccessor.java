package com.freezephoenix.fabric.bettermod.impl.mixin;

import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseSpawner.class)
public interface BaseSpawnerAccessor {
	@Accessor("nextSpawnData")
	@Nullable SpawnData bettermod$getNextSpawnData();
}
