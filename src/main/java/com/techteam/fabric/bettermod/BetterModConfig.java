package com.techteam.fabric.bettermod;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;

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
