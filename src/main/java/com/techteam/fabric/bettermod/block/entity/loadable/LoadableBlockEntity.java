package com.techteam.fabric.bettermod.block.entity.loadable;

import com.techteam.fabric.bettermod.block.entity.BetterBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class LoadableBlockEntity extends BetterBlockEntity {

    public LoadableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    public LoadableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, int size) {
        super(blockEntityType, blockPos, blockState, size);
    }

    @Override
    public void cancelRemoval() {
        super.cancelRemoval();
        if (this.getWorld().isClient()) {
            if (this instanceof IClientLoadableBlockEntity loadable) {
                loadable.onClientLoad();
            }
        } else if (this instanceof IServerLoadableBlockEntity loadable) {
            loadable.onServerLoad();
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (this.getWorld().isClient()) {
            if (this instanceof IClientLoadableBlockEntity loadable) {
                loadable.onClientUnload();
            }
        } else if (this instanceof IServerLoadableBlockEntity loadable) {
            loadable.onServerUnload();
        }
    }
}
