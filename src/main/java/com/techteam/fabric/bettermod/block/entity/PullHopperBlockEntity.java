package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IServerLoadableBlockEntity;
import com.techteam.fabric.bettermod.util.InventoryUtil;
import com.techteam.fabric.bettermod.util.Texts;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class PullHopperBlockEntity extends BetterHopperBlockEntity<PullHopperBlockEntity> implements IServerLoadableBlockEntity {
	public static final Identifier ID = Identifier.of("bettermod", "pull_hopper");

	public final InventoryStorage SELF = InventoryStorage.of(this.inventory, null);
	private BlockApiCache<Storage<ItemVariant>, Direction> PULL_TARGET_CACHE;
	private BlockApiCache<Storage<ItemVariant>, Direction> PUSH_TARGET_CACHE;

	public PullHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.PULL_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public boolean scheduledTick(World world, BlockPos pos, BlockState blockState) {
		boolean activated = false;
		// Push
		{
			Storage<ItemVariant> PUSH_TARGET = PUSH_TARGET_CACHE.find(blockState.get(HopperBlock.FACING).getOpposite());
			if(PUSH_TARGET != null) {
				activated = InventoryUtil.handleTransfer(SELF, PUSH_TARGET);
			}
		}
		// Pull
		{
			Storage<ItemVariant> PULL_TARGET = PULL_TARGET_CACHE.find(Direction.DOWN);
			if(PULL_TARGET != null) {
				activated = InventoryUtil.handleTransfer(PULL_TARGET, SELF) || activated;
			}
		}
		return activated;
	}

	@Override
	public Text getDisplayName() {
		return Texts.PULL_HOPPER;
	}

	@Override
	public void onServerLoad(ServerWorld world, BlockPos pos, BlockState state) {
		PUSH_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, world, pos.offset(state.get(HopperBlock.FACING)));
		PULL_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, world, pos.offset(Direction.UP));
	}

	@Override
	public void onServerUnload(ServerWorld world, BlockPos pos, BlockState state) {
		PUSH_TARGET_CACHE = null;
		PULL_TARGET_CACHE = null;
	}
}
