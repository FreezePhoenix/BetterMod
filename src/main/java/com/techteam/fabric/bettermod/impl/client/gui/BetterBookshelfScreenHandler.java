package com.techteam.fabric.bettermod.impl.client.gui;

import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.util.ItemTagKeys;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public final class BetterBookshelfScreenHandler extends SyncedGuiDescription {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("bettermod", "bookshelf");
	private static final int INVENTORY_SIZE = 16;

	public BetterBookshelfScreenHandler(int syncId, @NotNull Inventory playerInventory, Container inventory) {
		super(
				BetterMod.BOOKSHELF_SCREEN_HANDLER_TYPE,
				syncId,
				playerInventory,
				inventory,
				null
		);

		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);
		root.setSize(176, 149);
		WItemSlot slot = WItemSlot.of(blockInventory, 0, 8, 2);
		slot.setInputFilter(item -> item.is(ItemTagKeys.SHELVABLE));
		root.add(slot, 16, 17);
		root.add(this.createPlayerInventoryPanel(), 7, 55);
		root.validate(this);
	}

	public BetterBookshelfScreenHandler(int syncId, @NotNull Inventory playerInventory) {
		this(syncId, playerInventory, new SimpleContainer(INVENTORY_SIZE));
	}
}