package com.techteam.fabric.bettermod.client.gui;

import ;
import com.techteam.fabric.bettermod.BetterMod;
import com.techteam.fabric.bettermod.client.BoxPropertyDelegate;
import com.techteam.fabric.bettermod.util.InventoryUtil;
import io.github.cottonmc.cotton.gui.EmptyInventory;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.RailShape;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DebugStickItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.state.property.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;

public final class RoomControllerScreenHandler extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 1;
    private static final String[] STRINGS = {"X+", "X-", "Y+", "Y-", "Z+", "Z-"};

    public RoomControllerScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull PacketByteBuf buf) {
        this(syncId, playerInventory, ScreenHandlerContext.create(playerInventory.player.getWorld(), buf.readBlockPos()));
    }

    private Property selectedProperty = null;

    public RoomControllerScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, @NotNull ScreenHandlerContext context) {
        super(
                BetterMod.ROOM_CONTROLLER_SCREEN_HANDLER_TYPE, syncId, playerInventory,
                InventoryUtil.getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(
                        context
                )
        );
        BoxPropertyDelegate boxPropertyDelegate = (BoxPropertyDelegate) propertyDelegate;
//		this.updateSyncHandler(null);
        WPlainPanel root = new WPlainPanel();
        root.setInsets(new Insets(2, 7, 0, 7));
        this.titleVisible = false;
        setRootPanel(root);
        root.setSize(176, 168 + 9 + 9);
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
        slot.setInputFilter((item) -> {
            Block b = Block.getBlockFromItem(item.getItem());
            return b != Blocks.AIR
                    && !(b instanceof BlockEntityProvider)
                    && b.getDefaultState()
                    .isOpaque();
        });
        WPlayerInvPanel panel = this.createPlayerInventoryPanel();
        root.add(panel, 0, 72 + 18);
        selectedProperty = boxPropertyDelegate.getProperty(0);

        WDynamicSlider slider = new WDynamicSlider(0, 1, Axis.HORIZONTAL) {
            @Override
            public void addTooltip(@NotNull TooltipBuilder tooltip) {
                if(selectedProperty == null) {
                    tooltip.add(Text.of("property=null"));
                } else {
                    tooltip.add(Text.of("property=" + selectedProperty.getName()));
                }
            }
        };
        WDynamicSlider slider2 = new WDynamicSlider(0, 1, Axis.HORIZONTAL) {
            @Override
            public void addTooltip(@NotNull TooltipBuilder tooltip) {
                if(selectedProperty == null) {
                    tooltip.add(Text.of("null=null"));
                } else {
                    tooltip.add(Text.of(selectedProperty.getName() + "=" + selectedProperty.name(boxPropertyDelegate.get().get(selectedProperty))));
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
            if(selectedProperty instanceof DirectionProperty directionProperty) {
                var values = directionProperty.getValues();
                var enumValues = directionProperty.getType().getEnumConstants();
                var direction = boxPropertyDelegate.get().get(directionProperty);
                int index = 0;
                for (Direction enumValue : enumValues) {
                    if (values.contains(enumValue)) {
                        if (enumValue == direction) {
                            break;
                        }
                        index++;
                    }
                }
                slider2.setValue(index, true);
            } else if(selectedProperty instanceof BooleanProperty booleanProperty) {
                int new_value = 0;
                if(boxPropertyDelegate.get().get(booleanProperty)) {
                    new_value = 1;
                }
                slider2.setValue(new_value, true);
            } else if(selectedProperty instanceof IntProperty intProperty) {
                int new_value = boxPropertyDelegate.get().get(intProperty) - intProperty.getValues().stream().min(Integer::compare).get();
                slider2.setValue(new_value, true);
            } else if(selectedProperty instanceof EnumProperty<? extends Enum<?>> enumProperty) {
                slider2.setValue(boxPropertyDelegate.get().get((EnumProperty<? extends Enum<?>>) enumProperty).ordinal(), true);
            }
        });
        slider2.setValueChangeListener((value) -> {
            if(selectedProperty instanceof DirectionProperty directionProperty) {
                var values = directionProperty.getValues();
                var enumValues = directionProperty.getType().getEnumConstants();
                Direction direction = null;
                int curIndex = 0;
                for (Direction enumValue : enumValues) {
                    if (values.contains(enumValue)) {
                        if(curIndex == value) {
                            direction = enumValue;
                            break;
                        }
                        curIndex++;
                    }
                }
                boxPropertyDelegate.set(boxPropertyDelegate.get().with(directionProperty,direction));
            } else if(selectedProperty instanceof BooleanProperty booleanProperty) {
                boolean new_value = value == 1;
                boxPropertyDelegate.set(boxPropertyDelegate.get().with(booleanProperty,new_value));
            } else if(selectedProperty instanceof IntProperty intProperty) {
                int new_value = value + intProperty.getValues().stream().min(Integer::compare).get();
                boxPropertyDelegate.set(boxPropertyDelegate.get().with(intProperty, new_value));
            } else if(selectedProperty instanceof EnumProperty<? extends Enum<?>> enumProperty) {
                boxPropertyDelegate.set(boxPropertyDelegate.get().with((EnumProperty) enumProperty, (Enum) enumProperty.getType().getEnumConstants()[value]));
            }
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
                if(selectedProperty instanceof DirectionProperty directionProperty) {
                    var values = directionProperty.getValues();
                    var enumValues = directionProperty.getType().getEnumConstants();
                    var direction = boxPropertyDelegate.get().get(directionProperty);
                    int lindex = 0;
                    for (Direction enumValue : enumValues) {
                        if (values.contains(enumValue)) {
                            if (enumValue == direction) {
                                break;
                            }
                            lindex++;
                        }
                    }
                    slider2.setValue(lindex, true);
                } else if(selectedProperty instanceof BooleanProperty booleanProperty) {
                    int new_value = 0;
                    if(boxPropertyDelegate.get().get(booleanProperty)) {
                        new_value = 1;
                    }
                    slider2.setValue(new_value, true);
                } else if(selectedProperty instanceof IntProperty intProperty) {
                    int new_value = boxPropertyDelegate.get().get(intProperty) - intProperty.getValues().stream().min(Integer::compare).get();
                    slider2.setValue(new_value, true);
                } else if(selectedProperty instanceof EnumProperty<? extends Enum<?>> enumProperty) {
                    slider2.setValue(boxPropertyDelegate.get().get((EnumProperty<? extends Enum<?>>) enumProperty).ordinal(), true);
                }
            } else {
//                slider2.setMaxValue(0);
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
