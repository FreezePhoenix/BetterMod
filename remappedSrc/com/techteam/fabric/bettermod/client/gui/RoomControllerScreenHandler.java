package com.techteam.fabric.bettermod.client.gui;

import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.client.BoxPropertyDelegate;
import com.techteam.fabric.bettermod.util.InventoryUtil;
import io.github.cottonmc.cotton.gui.EmptyInventory;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public final class RoomControllerScreenHandler extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 1;
    private static final String[] STRINGS = {"X+", "X-", "Y+", "Y-", "Z+", "Z-"};

    public RoomControllerScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull PacketByteBuf buf) {
        this(syncId, playerInventory, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos()));
    }

    public RoomControllerScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull ScreenHandlerContext context) {
        super(
                BetterMod.ROOM_CONTROLLER_SCREEN_HANDLER_TYPE, syncId, playerInventory,
                InventoryUtil.getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(
                        context
                )
        );
//		this.updateSyncHandler(null);
        WPlainPanel root = new WPlainPanel();
        root.setInsets(new Insets(2, 7, 0, 7));
        this.titleVisible = false;
        setRootPanel(root);
        root.setSize(176, 168);
        for (int i = 0; i < 6; i++) {
            WBoundSlider slider = new WBoundSlider(0, 60, true, i) {
                @Override
                public void addTooltip(@NotNull TooltipBuilder tooltip) {
                    tooltip.add(Text.of(STRINGS[this.bound_index] + propertyDelegate.get(this.bound_index)));
                }
            };

            slider.setValue(
                    i % 2 == 1
                            ? 60 - propertyDelegate.get(i)
                            : propertyDelegate.get(i)
            );
            slider.setValueChangeListener((value) -> {
                propertyDelegate.set(
                        slider.bound_index,
                        slider.bound_index % 2 == 1
                                ? 60 - value
                                : value
                );
                ((BoxPropertyDelegate) propertyDelegate).sync();
            });
            if (i == 2
                    || i == 3) {
                root.add(slider, (i % 2) * 94, (i / 2) * 18, 68, 18);
            } else {
                root.add(slider, (i % 2) * 85, (i / 2) * 18, 77, 18);
            }
        }
        WSingleItemSlot slot = WSingleItemSlot.of(blockInventory, 0);
        root.add(slot, 72, 18);
        slot.setFilter((item) -> {
            Block b = Block.getBlockFromItem(item.getItem());
            return b != Blocks.AIR
                    && !(b instanceof BlockEntityProvider)
                    && b.getDefaultState()
                    .isOpaque();
        });
        WPlayerInvPanel panel = this.createPlayerInventoryPanel();
        root.add(panel, 0, 72);
        WSlider slider = new WSlider(0, propertyDelegate.get(7), Axis.HORIZONTAL) {
            @Override
            public void addTooltip(@NotNull TooltipBuilder tooltip) {
                tooltip.add(Text.of("VARIANT: " + propertyDelegate.get(6)));
            }
        };
        slider.setValue(propertyDelegate.get(6));
        slider.setValueChangeListener((value) -> {
            propertyDelegate.set(6, value);
            ((BoxPropertyDelegate) propertyDelegate).sync();
        });
        slot.addChangeListener((__, inventory, index, stack) -> {
            int current = slider.getValue();
            if (stack.isEmpty()) {
                slider.setMaxValue(0);
            } else {
                slider.setMaxValue(propertyDelegate.get(7) - 1);
            }
            if (current == slider.getValue()) {
                ((BoxPropertyDelegate) propertyDelegate).rerender();
            }
            inventory.markDirty();
        });
        root.add(slider, 0, 54, 176 - 14, 18);
        root.validate(this);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        ((BoxPropertyDelegate) propertyDelegate).sync();
    }
}
