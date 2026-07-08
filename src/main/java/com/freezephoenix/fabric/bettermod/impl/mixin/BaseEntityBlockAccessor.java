package com.freezephoenix.fabric.bettermod.impl.mixin;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BaseEntityBlock.class)
public interface BaseEntityBlockAccessor {
	@Invoker("createTickerHelper")
	static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> createTickerHelper(final BlockEntityType<A> actual, final BlockEntityType<E> expected, final @Nullable BlockEntityTicker<? super E> ticker) {
		throw new AssertionError();
	}
}
