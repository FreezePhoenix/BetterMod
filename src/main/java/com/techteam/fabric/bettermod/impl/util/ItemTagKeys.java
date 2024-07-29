package com.techteam.fabric.bettermod.impl.util;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ItemTagKeys {
	public static final TagKey<Item> SHELVABLE = TagKey.of(RegistryKeys.ITEM, Identifier.of("bettermod", "shelvable"));
}
