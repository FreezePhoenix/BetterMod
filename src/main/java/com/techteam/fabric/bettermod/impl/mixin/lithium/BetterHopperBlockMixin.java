package com.techteam.fabric.bettermod.impl.mixin.lithium;

import com.techteam.fabric.bettermod.api.block.BetterBlock;
import com.techteam.fabric.bettermod.impl.block.BetterHopperBlock;
import com.techteam.fabric.bettermod.impl.block.entity.BetterHopperBlockEntity;
import me.jellysquid.mods.lithium.common.hopper.UpdateReceiver;
import me.jellysquid.mods.lithium.common.world.blockentity.BlockEntityGetter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BetterHopperBlock.class)
public abstract class BetterHopperBlockMixin<T extends BetterHopperBlockEntity<T>> extends BetterBlock<T> {
	public BetterHopperBlockMixin(@NotNull Settings settings) {
		super(settings);
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "getStateForNeighborUpdate",
	        at = @At("HEAD")
	)
	private void notifyOnNeighborUpdate(BlockState myBlockState, Direction direction, BlockState newState, WorldAccess world, BlockPos myPos, BlockPos posFrom, CallbackInfoReturnable<BlockState> ci) {
		//invalidate cache when composters change state
		if (!world.isClient() && newState.getBlock() instanceof InventoryProvider) {
			this.updateHopper(world, myBlockState, myPos, posFrom);
		}
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "neighborUpdate",
	        at = @At(value = "HEAD")
	)
	private void updateBlockEntity(BlockState myBlockState, World world, BlockPos myPos, Block block, BlockPos posFrom, boolean moved, CallbackInfo ci) {
		//invalidate cache when the block is replaced
		if (!world.isClient()) {
			this.updateHopper(world, myBlockState, myPos, posFrom);
		}
	}

	@Unique
	private void updateHopper(WorldAccess world, BlockState myBlockState, BlockPos myPos, BlockPos posFrom) {
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
