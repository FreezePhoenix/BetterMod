package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.block.entity.loadable.IServerLoadableBlockEntity;
import com.techteam.fabric.bettermod.client.gui.BitHopperScreenHandler;
import com.techteam.fabric.bettermod.client.gui.PullHopperScreenHandler;
import com.techteam.fabric.bettermod.util.InventoryUtil;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class PullHopperBlockEntity extends TickOnInterval implements IServerLoadableBlockEntity {
	public static final Identifier ID = new Identifier("bettermod", "pull_hopper");

	public final InventoryStorage SELF = InventoryStorage.of(this.inventory, null);
	private BlockApiCache<Storage<ItemVariant>, Direction> PULL_TARGET_CACHE;
	private BlockApiCache<Storage<ItemVariant>, Direction> PUSH_TARGET_CACHE;
	public PullHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.PULL_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState, 5, 8);
	}

	@Override
	public void update(World world, BlockPos pos, BlockState blockState) {
		// Push
		{
			Storage<ItemVariant> PUSH_TARGET = PUSH_TARGET_CACHE.find(blockState.get(HopperBlock.FACING).getOpposite());
			if(PUSH_TARGET != null) {
				InventoryUtil.handleTransfer(SELF, PUSH_TARGET);
			}
		}
		// Pull
		{
			Storage<ItemVariant> PULL_TARGET = PULL_TARGET_CACHE.find(Direction.DOWN);
			if(PULL_TARGET != null) {
				InventoryUtil.handleTransfer(PULL_TARGET, SELF);
			}
		}
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	public Text getDisplayName() {
		return Text.of("Pull Hopper");
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new PullHopperScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
	}

	@Override
	public void onServerLoad(World world, BlockPos pos, BlockState state) {
		PUSH_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) world, pos.offset(state.get(HopperBlock.FACING)));
		PULL_TARGET_CACHE = BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) world, pos.offset(Direction.UP));
	}

	@Override
	public void onServerUnload(World world, BlockPos pos, BlockState state) {
		PUSH_TARGET_CACHE = null;
		PULL_TARGET_CACHE = null;
	}
}
