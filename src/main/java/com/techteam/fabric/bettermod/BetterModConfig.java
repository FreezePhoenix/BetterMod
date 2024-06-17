package com.techteam.fabric.bettermod;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "bettermod")
public class BetterModConfig implements ConfigData {
	public boolean LogRoomTransitions = true;
	public boolean LogRoomAllocations = true;

	@Override
	public void validatePostLoad() {
		BetterMod.CONFIG = this;
	}
}
