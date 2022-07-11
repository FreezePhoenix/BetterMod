package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.client.gui.BetterBookshelfScreenHandler;
import com.techteam.fabric.bettermod.client.gui.BitHopperScreenHandler;
import com.techteam.fabric.bettermod.util.InventoryUtil;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BitHopperBlockEntity extends TickOnInterval {
	public static final Identifier ID = new Identifier("bettermod", "bit_hopper");

	public BitHopperBlockEntity(@NotNull BlockPos blockPos, BlockState blockState) {
		super(BetterMod.BIT_HOPPER_BLOCK_ENTITY_TYPE, blockPos, blockState, 5, 8);
	}

	@Override
	public void update(World world, BlockPos pos, BlockState blockState) {
		Inventory inventory = InventoryUtil.getInventoryAt(world, pos.offset(blockState.get(HopperBlock.FACING)));
		if(inventory == null) {
			return;
		}
		InventoryStorage cap = InventoryStorage.of(inventory, blockState.get(HopperBlock.FACING).getOpposite());
		InventoryUtil.handleTransfer(InventoryStorage.of(this, null), cap);
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	protected Text getContainerName() {
		return Text.of("Bit Hopper");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new BitHopperScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
	}
}
