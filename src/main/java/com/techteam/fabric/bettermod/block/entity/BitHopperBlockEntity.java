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

public class BitHopperBlockEntity extends BetterHopperBlockEntity<BitHopperBlockEntity> implements IServerLoadableBlockEntity {
	public static final Identifier ID = Identifier.of("bettermod", "bit_hopper");
	private BlockApiCache<Storage<ItemVariant>, Direction> PUSH_TARGET_CACHE;

	public BitHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.BIT_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public boolean scheduledTick(World world, BlockPos pos, BlockState blockState) {
		Storage<ItemVariant> PUSH_TARGET = PUSH_TARGET_CACHE.find(blockState.get(HopperBlock.FACING).getOpposite());
		if (PUSH_TARGET != null) {
			return InventoryUtil.handleTransfer(SELF, PUSH_TARGET);
		}
		return false;
	}

	@Override
	public Text getDisplayName() {
		return Texts.BIT_HOPPER;
	}

	@Override
	public void onServerLoad(ServerWorld world, BlockPos pos, BlockState state) {
		PUSH_TARGET_CACHE = BlockApiCache.create(
				ItemStorage.SIDED,
				 world,
				pos.offset(state.get(HopperBlock.FACING))
		);
	}

	@Override
	public void onServerUnload(ServerWorld world, BlockPos pos, BlockState state) {
		PUSH_TARGET_CACHE = null;
	}
}
