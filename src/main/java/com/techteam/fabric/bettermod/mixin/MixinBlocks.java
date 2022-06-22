package com.techteam.fabric.bettermod.mixin;

import com.techteam.fabric.bettermod.block.BetterBookshelfBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class MixinBlocks {
    // Matches the one after TNT, which is Bookshelf
    @Redirect(method = "<clinit>",
              at = @At(value = "NEW",
                       target = "net/minecraft/block/Block", ordinal = 0),
              slice = @Slice(
                      from = @At(value = "CONSTANT", args = "stringValue=tnt")
              ), allow =  1, require = 1)
	private static Block blockConstructHook(AbstractBlock.Settings settings) {
        return new BetterBookshelfBlock(settings);
	}
}
