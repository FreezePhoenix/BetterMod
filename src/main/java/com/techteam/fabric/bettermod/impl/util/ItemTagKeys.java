package com.techteam.fabric.bettermod.impl.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTagKeys {
	public static final TagKey<Item> SHELVABLE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("bettermod", "shelvable"));
}
