package com.techteam.fabric.bettermod.api.hooks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IForceRender {
	default boolean forceRender() {
		return false;
	}
}
