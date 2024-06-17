package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.BetterMod;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerLogic.class)
public abstract class MixinMobSpawnerLogic {
    @Inject(method = "readNbt",
            at = @At("HEAD"))
    public void readNbt(@Nullable World world, BlockPos pos, @NotNull NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("SpawnData") && nbt.getCompound("SpawnData").contains("id")) {
            NbtCompound old_entry = nbt.getCompound("SpawnData");
            NbtCompound new_entry = new NbtCompound();
            new_entry.put("entity", old_entry);
            nbt.put("SpawnData", new_entry);
	        BetterMod.LOGGER.info("Converted SpawnData mapping from {} to {}", old_entry, new_entry);
        }
        if (nbt.contains("SpawnPotentials") && nbt.getList("SpawnPotentials", NbtElement.COMPOUND_TYPE).getCompound(0).contains("Weight")) {
            for (NbtElement ele : nbt.getList("SpawnPotentials", NbtElement.COMPOUND_TYPE)) {
                NbtCompound old_entry = (NbtCompound) ele;
                NbtCompound entry = new NbtCompound();
                NbtCompound data = new NbtCompound();
                data.put("entity", old_entry.getCompound("Entity").copy());
                entry.putInt("weight", old_entry.getInt("Weight"));
                entry.put("data", data);
                old_entry.copyFrom(entry);
            }
        }
    }
}
