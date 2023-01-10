package com.techteam.fabric.bettermod.client.gui;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.util.InventoryUtil;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import org.jetbrains.annotations.NotNull;

public final class BetterBookshelfScreenHandler extends SyncedGuiDescription {
	private static final int INVENTORY_SIZE = 16;

	public BetterBookshelfScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull PacketByteBuf buf) {
		this(syncId, playerInventory, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos()));
	}

	public BetterBookshelfScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull ScreenHandlerContext context) {
		super(
				BetterMod.BOOKSHELF_SCREEN_HANDLER_TYPE,
				syncId,
				playerInventory,
				InventoryUtil.getBlockInventory(context, INVENTORY_SIZE),
				getBlockPropertyDelegate(context)
		);

		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);
		root.setSize(176, 149);
		WItemSlot slot = WItemSlot.of(blockInventory, 0, 8, 2);
		slot.setFilter((final ItemStack item) -> {

			Item _item = item.getItem();
			return _item == Items.BOOK || _item == Items.ENCHANTED_BOOK || _item == Items.WRITTEN_BOOK || _item == Items.WRITABLE_BOOK || _item == Items.FILLED_MAP || _item == Items.MAP || _item == Items.PAPER;
		});
		root.add(slot, 16, 17);
		root.add(this.createPlayerInventoryPanel(), 7, 55);
		root.validate(this);
	}
}