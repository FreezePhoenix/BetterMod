package com.techteam.fabric.bettermod.block.entity.loadable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface IClientLoadableBlockEntity {
    @Environment(EnvType.CLIENT)
    void onClientLoad();

    @Environment(EnvType.CLIENT)
    void onClientUnload();
}
