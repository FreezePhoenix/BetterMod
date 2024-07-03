package com.techteam.fabric.bettermod.impl.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.api.block.entity.loadable.IServerLoadableBlockEntity;
import com.techteam.fabric.bettermod.impl.client.gui.BitHopperScreenHandler;
import com.techteam.fabric.bettermod.impl.util.InventoryUtil;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BitHopperBlockEntity extends TickOnInterval implements IServerLoadableBlockEntity {
	public static final Identifier ID = new Identifier("bettermod", "bit_hopper");
	public final InventoryStorage SELF = InventoryStorage.of(this.inventory, null);
	private BlockApiCache<Storage<ItemVariant>, Direction> PUSH_TARGET_CACHE;

	public BitHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.BIT_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState, 5, 8);
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
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	public Text getDisplayName() {
		return Text.of("Bit Hopper");
	}
	@Contract("_, _, _ -> new")
	@Override
	public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new BitHopperScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
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
