package com.freezephoenix.fabric.bettermod.impl.mixin;

import com.freezephoenix.fabric.bettermod.impl.util.LootTableIdentifiers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(SpawnerBlock.class)
public abstract class MixinSpawnerBlock extends Block {
	public MixinSpawnerBlock(Properties properties) {
		super(properties);
	}

	@Override
	@Intrinsic
	protected List<ItemStack> getDrops(final BlockState state, final LootParams.Builder params) {
		return super.getDrops(state, params);
	}

	@Inject(
			method = "getDrops",
			at = @At("HEAD")
	)
	public void getDropsCallback(final BlockState state, final LootParams.Builder params, CallbackInfoReturnable<List<ItemStack>> ci) {
		BlockEntity maybeEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (maybeEntity instanceof SpawnerBlockEntity entity) {
			if (entity.getSpawner() instanceof BaseSpawnerAccessor spawnerAccessor) {
				var spawnData = spawnerAccessor.bettermod$getNextSpawnData();
				if (spawnData != null) {
					var spawning = spawnData.getEntityToSpawn().read("id", Identifier.CODEC);
					if (spawning.isPresent() && spawning.get().equals(EntityTypeIds.SILVERFISH.identifier())) {
						params.withDynamicDrop(
								LootTableIdentifiers.SPAWNER_DYNAMIC_DROP_ID, (output) -> {
									var book = Items.WRITTEN_BOOK.getDefaultInstance();
									var content = """
											Authors
											 - FreezePhoenix
											
											Bug Reporters
											 - MasterGamerReg
											""";
									book.set(
											DataComponents.WRITTEN_BOOK_CONTENT,
											new WrittenBookContent(
													new Filterable<>("Better Mod v2.0.3 credits", Optional.empty()),
													"FreezePhoenix",
													0,
													List.of(
															new Filterable<>(
																	MutableComponent.create(new PlainTextContents.LiteralContents(
																			content)), Optional.empty()
															)
													),
													true
											)
									);
									output.accept(book);
								}
						);
					}
				}
			}
		}
	}
}