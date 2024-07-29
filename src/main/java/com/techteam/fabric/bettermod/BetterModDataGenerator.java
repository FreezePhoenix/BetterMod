package com.techteam.fabric.bettermod;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class BetterModDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(TagGenerator::new);
	}

	private static class TagGenerator extends FabricTagProvider.ItemTagProvider {
		private static final TagKey<Item> SHELVABLE_ITEMS = TagKey.of(
				RegistryKeys.ITEM,
				Identifier.of("bettermod", "shelvable")
		);

		public TagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			getOrCreateTagBuilder(SHELVABLE_ITEMS)
					.addOptionalTag(ItemTags.BOOKSHELF_BOOKS)
					.add(Items.PAPER)
					.add(Items.FILLED_MAP)
					.add(Items.MAP);
		}
	}

}