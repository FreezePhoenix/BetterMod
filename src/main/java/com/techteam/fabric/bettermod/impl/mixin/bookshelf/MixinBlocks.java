package com.techteam.fabric.bettermod.impl.mixin.bookshelf;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.techteam.fabric.bettermod.impl.block.BetterBookshelfBlock;
import net.minecraft.references.BlockItemId;
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
	private static Block register(final BlockItemId id, final Function<BlockBehaviour.Properties, Block> factory, final BlockBehaviour.Properties properties) {
		throw new UnsupportedOperationException();
	}
	@Definition(id = "register", method = "Lnet/minecraft/world/level/block/Blocks;register(Lnet/minecraft/references/BlockItemId;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;")
	@Definition(id = "BOOKSHELF", field = "Lnet/minecraft/references/BlockItemIds;BOOKSHELF:Lnet/minecraft/references/BlockItemId;")
	@Expression("register(BOOKSHELF, ?)")
	@Redirect(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static Block blockConstructHook(final BlockItemId id, final BlockBehaviour.Properties properties) {
		return register(id, BetterBookshelfBlock::new, properties);
	}
}
