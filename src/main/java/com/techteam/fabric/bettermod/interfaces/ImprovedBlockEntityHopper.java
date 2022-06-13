package com.techteam.fabric.bettermod.interfaces;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.entity.Hopper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public interface ImprovedBlockEntityHopper extends Hopper {
    @NotNull HopperCache getCache();

    boolean invokeIsFull();

    class HopperCache {
        public @NotNull ObjectArrayList<Inventory> INPUT_INV = new ObjectArrayList<>();
        public @NotNull ObjectArrayList<ItemEntity> ITEMS = new ObjectArrayList<>();
        public @NotNull ObjectArrayList<Inventory> OUTPUT_INV = new ObjectArrayList<>();

        public void clear() {
            ITEMS.clear();
            INPUT_INV.clear();
            OUTPUT_INV.clear();
        }

        public void trim() {
            ITEMS.trim();
            INPUT_INV.trim();
            OUTPUT_INV.trim();
        }
    }
}
