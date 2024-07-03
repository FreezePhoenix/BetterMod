package com.techteam.fabric.bettermod.impl.client.gui;

import com.google.common.collect.Iterators;
import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.client.BoxPropertyDelegate;
import com.techteam.fabric.bettermod.impl.util.InventoryUtil;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.state.property.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class RoomControllerScreenHandler extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 1;
    private static final String[] STRINGS = {"X-", "X+", "Y-", "Y+", "Z-", "Z+"};

    public RoomControllerScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull BlockPos pos) {
        this(syncId, playerInventory, ScreenHandlerContext.create(playerInventory.player.getWorld(), pos));
    }

    private Property<?> selectedProperty;

    private static <T extends Comparable<T>> String getValueString(BlockState state, Property<T> property) {
        return property.name(state.get(property));
    }

	private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, int index) {
		return state.with(property, Iterators.get(property.getValues().iterator(), index));
	}

	private static <T extends Comparable<T>> int value(BlockState state, Property<T> property) {
		return Iterators.indexOf(property.getValues().iterator(), state.get(property)::equals);
	}

    public RoomControllerScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull ScreenHandlerContext context) {
        super(
                BetterMod.ROOM_CONTROLLER_SCREEN_HANDLER_TYPE, syncId, playerInventory,
                InventoryUtil.getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(
                        context
                )
        );
        BoxPropertyDelegate boxPropertyDelegate = (BoxPropertyDelegate) propertyDelegate;
        WPlainPanel root = new WPlainPanel();
        root.setInsets(new Insets(2, 7, 0, 7));
        setRootPanel(root);
		setTitleVisible(false);
        root.setSize(176, 168 + 9 + 9);
        for (int i = 0; i < 6; i++) {
            WBoundSlider slider = new WBoundSlider(0, 63, true, i) {
                @Override
                public void addTooltip(@NotNull TooltipBuilder tooltip) {
                    tooltip.add(Text.of(STRINGS[this.bound_index] + propertyDelegate.get(this.bound_index)));
                }
            };

            slider.setValue(
                    i % 2 == 1
                            ? propertyDelegate.get(i)
                            : 63 - propertyDelegate.get(i)
            );
            slider.setValueChangeListener((value) -> {
                propertyDelegate.set(
                        slider.bound_index,
                        slider.bound_index % 2 == 1
                                ? value
                                : 63 - value
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
	    WItemSlot slot = WItemSlot.of(blockInventory, 0);
        root.add(slot, 72, 18);
        slot.setInputFilter((item) -> {
            Block b = Block.getBlockFromItem(item.getItem());
            return b != Blocks.AIR
                    && !(b instanceof BlockEntityProvider)
                    && !RenderLayers.getBlockLayer(b.getDefaultState()).isTranslucent();
        });
        WPlayerInvPanel panel = this.createPlayerInventoryPanel();
        root.add(panel, 0, 72 + 18);
        selectedProperty = boxPropertyDelegate.getProperty(0);

        WSlider slider = new WSlider(0, 1, Axis.HORIZONTAL) {
            @Override
            public void addTooltip(@NotNull TooltipBuilder tooltip) {
                if(selectedProperty == null) {
                    tooltip.add(Text.of("property=null"));
                } else {
                    tooltip.add(Text.of("property=" + selectedProperty.getName()));
                }
            }
        };
        WSlider slider2 = new WSlider(0, 1, Axis.HORIZONTAL) {
            @Override
            public void addTooltip(@NotNull TooltipBuilder tooltip) {
                if(selectedProperty == null) {
                    tooltip.add(Text.of("null=null"));
                } else {
                    tooltip.add(Text.of(selectedProperty.getName() + "=" + getValueString(boxPropertyDelegate.get(), selectedProperty)));
                }
            }
        };
        slider.setValueChangeListener((value) -> {
            selectedProperty = boxPropertyDelegate.getProperty(value);
            if(selectedProperty == null) {
                slider2.setMaxValue(0);
                return;
            }
            slider2.setMaxValue(selectedProperty.getValues().size() - 1);
	        slider2.setValue(RoomControllerScreenHandler.value(boxPropertyDelegate.get(), selectedProperty));
        });

        slider2.setValueChangeListener((value) -> {
	        boxPropertyDelegate.set(RoomControllerScreenHandler.with(boxPropertyDelegate.get(), selectedProperty, value));
            ((BoxPropertyDelegate) propertyDelegate).sync();
        });

        slot.addChangeListener((__, inventory, index, stack) -> {
            if(!boxPropertyDelegate.get().isOf(Block.getBlockFromItem(stack.getItem()))) {
                boxPropertyDelegate.set(Block.getBlockFromItem(stack.getItem()).getDefaultState());
            }
            selectedProperty = boxPropertyDelegate.getProperty(0);
            if(boxPropertyDelegate.properties() == 0) {
                slider.setMaxValue(0);
            } else {
                slider.setMaxValue(boxPropertyDelegate.properties() - 1);
            }
            slider.setValue(0);
            if(selectedProperty != null) {
                slider2.setMaxValue(selectedProperty.getValues().size() - 1);
	            slider2.setValue(RoomControllerScreenHandler.value(boxPropertyDelegate.get(), selectedProperty));
            }
            boxPropertyDelegate.rerender();
            inventory.markDirty();
        });
        root.add(slider, 0, 54, 176 - 14, 18);
        root.add(slider2, 0, 54 + 18, 176 - 14, 18);
        root.validate(this);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        ((BoxPropertyDelegate) propertyDelegate).sync();
    }
}
