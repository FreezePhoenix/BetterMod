package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.hooks.HopperHooks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

@Mixin(value=LootableContainerBlockEntity.class, priority=1000)
public abstract class MixinLootableContainerBlockEntity {
//    @Shadow
//    protected abstract @NotNull DefaultedList<ItemStack> getInvStackList();
//
//    @Inject(
//            method = "isEmpty()Z",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/block/entity/LootableContainerBlockEntity;getInvStackList()Lnet/minecraft/util/collection/DefaultedList;"
//            ),
//            cancellable = true
//    )
//    public void isEmptyHook(@NotNull CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(HopperHooks.isEmptyHook(getInvStackList()));
//    }
}
