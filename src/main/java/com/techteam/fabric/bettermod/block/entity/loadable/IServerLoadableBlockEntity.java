package com.techteam.fabric.bettermod.block.entity.loadable;

public interface IServerLoadableBlockEntity {
    void onServerLoad();
    /**
     * The world should always be valid when this method is called.
     */
    void onServerUnload();
}
