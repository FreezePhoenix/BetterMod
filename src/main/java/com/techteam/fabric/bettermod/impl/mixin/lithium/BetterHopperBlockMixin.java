package com.techteam.fabric.bettermod.impl.mixin.lithium;

import com.techteam.fabric.bettermod.api.block.BetterBlock;
import com.techteam.fabric.bettermod.impl.block.BetterHopperBlock;
import com.techteam.fabric.bettermod.impl.block.entity.BetterHopperBlockEntity;
import net.caffeinemc.mods.lithium.common.block.entity.ShapeUpdateHandlingBlockBehaviour;
import net.caffeinemc.mods.lithium.common.hopper.UpdateReceiver;
import net.caffeinemc.mods.lithium.common.world.blockentity.BlockEntityGetter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BetterHopperBlock.class)
public abstract class BetterHopperBlockMixin<T extends BetterHopperBlockEntity<T>> extends BetterBlock<T> implements ShapeUpdateHandlingBlockBehaviour {
	public BetterHopperBlockMixin(@NotNull Settings settings) {
		super(settings);
	}

	@Override
	public void lithium$handleShapeUpdate(WorldView world, BlockState myBlockState, BlockPos myPos, BlockPos posFrom, BlockState newState) {
		//invalidate cache when composters change state
		if (!world.isClient() && newState.getBlock() instanceof InventoryProvider) {
			this.updateHopper(world, myBlockState, myPos, posFrom);
		}
	}

	@Inject(method = "neighborUpdate",
	        at = @At(value = "HEAD")
	)
	private void updateBlockEntity(BlockState state, World world, BlockPos pos, Block sourceBlock, WireOrientation wireOrientation, boolean notify, CallbackInfo ci) {
		//invalidate cache when the block is replaced
		if (!world.isClient()) {
			BlockEntity hopper = ((BlockEntityGetter) world).lithium$getLoadedExistingBlockEntity(pos);
			if (hopper instanceof UpdateReceiver updateReceiver) {
				updateReceiver.lithium$invalidateCacheOnUndirectedNeighborUpdate();
			}
		}
	}

	@Unique
	private void updateHopper(WorldView world, BlockState myBlockState, BlockPos myPos, BlockPos posFrom) {
		Direction facing = myBlockState.get(HopperBlock.FACING);
		boolean above = posFrom.getY() == myPos.getY() + 1;
		if (above || posFrom.getX() == myPos.getX() + facing.getOffsetX() && posFrom.getY() == myPos.getY() + facing.getOffsetY() && posFrom.getZ() == myPos.getZ() + facing.getOffsetZ()) {
			BlockEntity hopper = ((BlockEntityGetter) world).lithium$getLoadedExistingBlockEntity(myPos);
			if (hopper instanceof UpdateReceiver updateReceiver) {
				updateReceiver.lithium$invalidateCacheOnNeighborUpdate(above);
			}
		}
	}
}
