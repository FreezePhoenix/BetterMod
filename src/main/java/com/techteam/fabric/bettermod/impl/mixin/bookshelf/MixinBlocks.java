package com.techteam.fabric.bettermod.impl.mixin.bookshelf;

import com.techteam.fabric.bettermod.impl.block.BetterBookshelfBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(Blocks.class)
public abstract class MixinBlocks {

	@Shadow
	private static Block register(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings) {
		throw new UnsupportedOperationException();
	}

	@Redirect(method = "<clinit>",
			  at = @At(value = "INVOKE",
					   target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;",
					   ordinal = 0),
			  slice = @Slice(from = @At(value = "CONSTANT",
										args = "stringValue=bookshelf")))
	private static Block blockConstructHook(String id, BlockBehaviour.Properties settings) {
		return register(id, BetterBookshelfBlock::new, settings);
	}
}
