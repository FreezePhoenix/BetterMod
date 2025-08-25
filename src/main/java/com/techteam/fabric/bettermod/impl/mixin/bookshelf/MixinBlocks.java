package com.techteam.fabric.bettermod.impl.mixin.bookshelf;

import com.techteam.fabric.bettermod.impl.block.BetterBookshelfBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

@Mixin(Blocks.class)
public abstract class MixinBlocks {

	@Shadow
	private static Block register(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
		throw new UnsupportedOperationException();
	}

	@Redirect(method = "<clinit>",
	          at = @At(value = "INVOKE",
	                   target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;",
	                   ordinal = 0),
	          slice = @Slice(from = @At(value = "CONSTANT",
	                                    args = "stringValue=bookshelf")))
	private static Block blockConstructHook(String id, AbstractBlock.Settings settings) {
		return register(id, BetterBookshelfBlock::new, settings);
	}
}
