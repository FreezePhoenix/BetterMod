package com.techteam.fabric.bettermod.block.entity.loadable;

public interface IServerLoadableBlockEntity {
    void onServerLoad();
    void onServerUnload();
}
