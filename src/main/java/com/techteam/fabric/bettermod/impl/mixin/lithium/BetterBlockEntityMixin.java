package com.techteam.fabric.bettermod.impl.mixin.lithium;

import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import net.caffeinemc.mods.lithium.api.inventory.LithiumInventory;
import net.caffeinemc.mods.lithium.common.block.entity.inventory_change_tracking.InventoryChangeEmitter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BetterBlockEntity.class)
public abstract class BetterBlockEntityMixin implements LithiumInventory {
	@Shadow
	protected DefaultedList<ItemStack> inventory;

	public DefaultedList<ItemStack> getInventoryLithium() {
		return this.inventory;
	}

	public void setInventoryLithium(DefaultedList<ItemStack> newInventory) {
		this.inventory = newInventory;
	}

	@Inject(
			method = "setHeldStacks",
			at = @At("RETURN")
	)
	private void emitStackListReplaced(DefaultedList<ItemStack> inventory, CallbackInfo callbackInfo) {
		if (this instanceof InventoryChangeEmitter inventoryChangeEmitter) {
			inventoryChangeEmitter.lithium$emitStackListReplaced();
		}
	}
}
