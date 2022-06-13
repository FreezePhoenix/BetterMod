package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.client.gui.BetterBookshelfScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BetterBookshelfBlockEntity extends BetterBlockEntity {
    public BetterBookshelfBlockEntity(@NotNull BlockPos pos, BlockState state) {
        super(BetterMod.BOOKSHELF_BLOCK_ENTITY_TYPE, pos, state, 16);
    }

    @Contract("_, _ -> new")
    @Override
    protected @NotNull ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BetterBookshelfScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
    }

    @Contract(value = " -> !null",
              pure = true)
    @Override
    protected Text getContainerName() {
        return Text.of("Bookshelf");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, @NotNull PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
