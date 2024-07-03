package com.techteam.fabric.bettermod.impl.mixin;

import com.techteam.fabric.bettermod.impl.block.BetterBookshelfBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class MixinBlocks {
	@Redirect(method = "<clinit>",
	          at = @At(value = "NEW",
	                   target = "net/minecraft/block/Block",
	                   ordinal = 0),
	          slice = @Slice(from = @At(value = "CONSTANT",
	                                    args = "stringValue=bookshelf")))
	private static Block blockConstructHook(AbstractBlock.Settings settings) {
		return new BetterBookshelfBlock(settings);
	}
}
