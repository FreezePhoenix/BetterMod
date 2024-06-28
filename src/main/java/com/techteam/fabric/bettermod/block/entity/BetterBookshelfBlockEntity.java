package com.techteam.fabric.bettermod.block.entity;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.api.block.entity.BetterBlockEntity;
import com.techteam.fabric.bettermod.client.gui.BetterBookshelfScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BetterBookshelfBlockEntity extends BetterBlockEntity {
	public static final Identifier ID = Identifier.of("minecraft", "bookshelf");

	public BetterBookshelfBlockEntity(@NotNull BlockPos pos, BlockState state) {
        super(BetterMod.BOOKSHELF_BLOCK_ENTITY_TYPE, pos, state, 16);
    }

    @Contract("_, _, _ -> new")
    @Override
    public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new BetterBookshelfScreenHandler(syncId, playerInventory, this.getInventory());
    }

    @Contract(value = " -> !null",
              pure = true)
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.minecraft.bookshelf");
    }

    @Override
    public boolean isValid(int slot, @NotNull ItemStack stack) {
        Item _item = stack.getItem();
        if(_item == Items.BOOK || _item == Items.ENCHANTED_BOOK || _item == Items.WRITTEN_BOOK || _item == Items.WRITABLE_BOOK || _item == Items.FILLED_MAP || _item == Items.MAP || _item == Items.PAPER) {
            return super.isValid(slot, stack);
        }
        return false;
    }
}
