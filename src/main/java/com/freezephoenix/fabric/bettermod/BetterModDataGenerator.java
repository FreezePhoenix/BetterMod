package com.freezephoenix.fabric.bettermod;

import com.freezephoenix.fabric.bettermod.impl.BetterMod;
import com.freezephoenix.fabric.bettermod.impl.block.BitHopperBlock;
import com.freezephoenix.fabric.bettermod.impl.block.PullHopperBlock;
import com.freezephoenix.fabric.bettermod.impl.block.StickHopperBlock;
import com.freezephoenix.fabric.bettermod.impl.util.ItemTagKeys;
import com.freezephoenix.fabric.bettermod.impl.util.LootTableIdentifiers;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.references.BlockItemId;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BetterModDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(LootTableGenerator::new);
		pack.addProvider(BetterModRecipeGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
	}

	private static class BlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
		public BlockTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		protected BlockItemTagAppender<Block> tag(final TagKey<Block> tag) {
			return new BlockItemTagAppender<>(super.tag(tag)) {
				protected ResourceKey<Block> convertElement(final BlockItemId element) {
					return element.block();
				}
			};
		}

		@Override
		protected void addTags(HolderLookup.Provider arg) {
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
					StickHopperBlock.BlockItemID,
					PullHopperBlock.BlockItemID,
					BitHopperBlock.BlockItemID
			);
		}
	}

	private static class ItemTagGenerator extends FabricTagsProvider.ItemTagsProvider {
		public ItemTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider arg) {
			builder(ItemTagKeys.SHELVABLE).forceAddTag(ItemTags.BOOKSHELF_BOOKS)
										  .add(ItemIds.PAPER)
										  .add(ItemIds.FILLED_MAP)
										  .add(ItemIds.MAP);
		}
	}

	private static class LootTableGenerator extends FabricBlockLootSubProvider {
		protected LootTableGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(dataOutput, registriesFuture);
		}

		private static Function<Block, LootTable.Builder> dynamic(final Identifier key) {
			return (block) -> LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
					DynamicLoot.dynamicEntry(key)));
		}

		@Override
		public void generate() {
			add(Blocks.SPAWNER, dynamic(LootTableIdentifiers.SPAWNER_DYNAMIC_DROP_ID));
			add(BetterMod.BIT_HOPPER_BLOCK, this::createNameableBlockEntityTable);
			add(BetterMod.PULL_HOPPER_BLOCK, this::createNameableBlockEntityTable);
			add(BetterMod.STICK_HOPPER_BLOCK, this::createNameableBlockEntityTable);
			add(
					Blocks.BOOKSHELF, (original) -> {
						return LootTable.lootTable()
										.withPool(LootPool.lootPool()
														  .setRolls(ConstantValue.exactly(1.0F))
														  .add((LootItem.lootTableItem(original)
																		.when(this.hasSilkTouch())
																		.apply(CopyComponentsFunction.copyComponentsFromBlockEntity(
																											 LootContextParams.BLOCK_ENTITY)
																									 .include(
																											 DataComponents.CUSTOM_NAME))).otherwise(
																  LootItem.lootTableItem(Items.BOOK)
																		  .apply(SetItemCountFunction.setCount(
																				  ConstantValue.exactly(3.0F))))));
					}
			);
			add(
					BetterMod.ROOM_CONTROLLER_BLOCK,
					LootTable.lootTable()
							 .withPool(LootPool.lootPool()
											   .add(LootItem.lootTableItem(Items.GOLD_INGOT)
															.apply(SetItemCountFunction.setCount(new ConstantValue(4)))))
							 .withPool(LootPool.lootPool()
											   .add(LootItem.lootTableItem(Items.REDSTONE)
															.apply(SetItemCountFunction.setCount(new ConstantValue(4)))))
							 .withPool(LootPool.lootPool().add(

									 LootItem.lootTableItem(Items.ENDER_PEARL)))
			);
		}
	}

	private static class BetterModRecipeGenerator extends FabricRecipeProvider {
		public BetterModRecipeGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
			return new RecipeProvider(registryLookup, exporter) {
				@Override
				public void buildRecipes() {
					shaped(RecipeCategory.REDSTONE, BetterMod.SLING_MECHANISM).pattern("IRI")
																			  .pattern(" I ")
																			  .pattern(" I ")
																			  .define('I', Items.STICK)
																			  .define('R', Items.STRING)
																			  .unlockedBy(
																					  getHasName(Items.STRING),
																					  has(Items.STRING)
																			  )
																			  .save(output);
					shaped(RecipeCategory.REDSTONE, Blocks.DISPENSER).pattern("###")
																	 .pattern("#X#")
																	 .pattern("#R#")
																	 .define('R', Items.REDSTONE)
																	 .define('#', Blocks.COBBLESTONE)
																	 .define('X', BetterMod.SLING_MECHANISM)
																	 .unlockedBy(
																			 getHasName(BetterMod.SLING_MECHANISM),
																			 has(BetterMod.SLING_MECHANISM)
																	 )
																	 .save(this.output);
					shaped(RecipeCategory.REDSTONE, BetterMod.BIT_HOPPER_BLOCK).pattern("   ")
																			   .pattern("ICI")
																			   .pattern(" I ")
																			   .define('I', Items.IRON_INGOT)
																			   .define('C', Items.CHEST)
																			   .unlockedBy(
																					   getHasName(Items.IRON_INGOT),
																					   has(Items.IRON_INGOT)
																			   )
																			   .save(output);
					shaped(RecipeCategory.REDSTONE, BetterMod.PULL_HOPPER_BLOCK).pattern("ICI")
																				.pattern("I I")
																				.pattern(" I ")
																				.define('I', Items.IRON_INGOT)
																				.define('C', Items.CHEST)
																				.unlockedBy(
																						getHasName(Items.IRON_INGOT),
																						has(Items.IRON_INGOT)
																				)
																				.save(output);
					shaped(RecipeCategory.REDSTONE, BetterMod.STICK_HOPPER_BLOCK).pattern("IRI")
																				 .pattern("ICI")
																				 .pattern("SIS")
																				 .define('I', Items.IRON_INGOT)
																				 .define('R', Items.COMPARATOR)
																				 .define('C', Items.CHEST)
																				 .define('S', Items.SLIME_BALL)
																				 .unlockedBy(
																						 getHasName(Items.SLIME_BALL),
																						 has(Items.SLIME_BALL)
																				 )
																				 .save(output);
				}
			};
		}

		@Override
		public String getName() {
			return "BetterMod";
		}
	}
}