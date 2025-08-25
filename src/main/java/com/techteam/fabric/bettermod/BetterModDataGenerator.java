package com.techteam.fabric.bettermod;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.block.entity.BetterBookshelfBlockEntity;
import com.techteam.fabric.bettermod.impl.util.ItemTagKeys;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlag;

import java.util.concurrent.CompletableFuture;

public class BetterModDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(TagGenerator::new);
		pack.addProvider(LootTableGenerator::new);
		pack.addProvider(BetterModRecipeGenerator::new);
	}

	private static class TagGenerator extends FabricTagProvider.ItemTagProvider {
		public TagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup arg) {
			valueLookupBuilder(ItemTagKeys.SHELVABLE)
					.forceAddTag(ItemTags.BOOKSHELF_BOOKS)
					.add(Items.PAPER)
					.add(Items.FILLED_MAP)
					.add(Items.MAP);
		}
	}

	private static class LootTableGenerator extends FabricBlockLootTableProvider {
		protected LootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(dataOutput, registriesFuture);
		}

		@Override
		public void generate() {
			addDrop(BetterMod.BIT_HOPPER_BLOCK);
			addDrop(BetterMod.PULL_HOPPER_BLOCK);
			addDrop(BetterMod.STICK_HOPPER_BLOCK);
			addDrop(BetterMod.ROOM_CONTROLLER_BLOCK, LootTable.builder().pool(
					LootPool.builder().with(
							ItemEntry.builder(Items.GOLD_INGOT)
									 .apply(SetCountLootFunction.builder(new ConstantLootNumberProvider(4))))
			).pool(
					LootPool.builder().with(
							ItemEntry.builder(Items.REDSTONE)
									 .apply(SetCountLootFunction.builder(new ConstantLootNumberProvider(4))))
			).pool(
					LootPool.builder().with(
							ItemEntry.builder(Items.ENDER_PEARL))
			));
		}
	}

	private static class BetterModRecipeGenerator extends FabricRecipeProvider {
		public BetterModRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
			return new RecipeGenerator(registryLookup, exporter) {
				@Override
				public void generate() {
					createShaped(RecipeCategory.REDSTONE, BetterMod.BIT_HOPPER_BLOCK)
							.pattern("   ")
							.pattern("ICI")
							.pattern(" I ")
							.input('I', Items.IRON_INGOT)
							.input('C', Items.CHEST)
							.criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
							.offerTo(exporter);
					createShaped(RecipeCategory.REDSTONE, BetterMod.PULL_HOPPER_BLOCK)
							.pattern("ICI")
							.pattern("I I")
							.pattern(" I ")
							.input('I', Items.IRON_INGOT)
							.input('C', Items.CHEST)
							.criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
							.offerTo(exporter);
					createShaped(RecipeCategory.REDSTONE, BetterMod.STICK_HOPPER_BLOCK)
							.pattern("ISI")
							.pattern("ICI")
							.pattern("SIS")
							.input('I', Items.IRON_INGOT)
							.input('C', Items.CHEST)
							.input('S', Items.SLIME_BALL)
							.criterion(hasItem(Items.SLIME_BALL), conditionsFromItem(Items.SLIME_BALL))
							.offerTo(exporter);
				}
			};
		}

		@Override
		public String getName() {
			return "BetterMod";
		}
	}
}