package com.techteam.fabric.bettermod.impl;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "bettermod")
public class BetterModConfig implements ConfigData {

	@Override
	public void validatePostLoad() {
		BetterMod.CONFIG = this;
	}
}
