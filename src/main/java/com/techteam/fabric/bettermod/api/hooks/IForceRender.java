package com.techteam.fabric.bettermod.api.hooks;

public interface IForceRender {
	default boolean forceRender() {
		return false;
	}
}
