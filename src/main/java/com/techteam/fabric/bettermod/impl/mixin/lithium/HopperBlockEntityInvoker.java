package com.techteam.fabric.bettermod.impl.mixin.lithium;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HopperBlockEntity.class)
public interface HopperBlockEntityInvoker {
	@Invoker("getBlockInventoryAt")
	static Inventory invokeGetBlockInventoryAt(World world, BlockPos pos, BlockState state) {
		return null;
	}
	@Invoker("canExtract")
	static boolean invokeCanExtract(Inventory toInventory, Inventory fromInventory, ItemStack stack, int slot, Direction facing) {
		return false;
	}
}