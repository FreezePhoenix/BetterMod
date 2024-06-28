package com.techteam.fabric.bettermod;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "bettermod")
public class BetterModConfig implements ConfigData {
	@ConfigEntry.Gui.Tooltip
	public boolean LogRoomTransitions = false;
	@ConfigEntry.Gui.Tooltip
	public boolean LogRoomAllocations = false;
	@Override
	public void validatePostLoad() {
		BetterMod.CONFIG = this;
	}
}
