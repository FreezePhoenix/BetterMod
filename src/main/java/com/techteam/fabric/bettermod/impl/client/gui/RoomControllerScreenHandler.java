package com.techteam.fabric.bettermod.impl.client.gui;

import com.google.common.collect.Iterables;
import com.techteam.fabric.bettermod.impl.BetterMod;
import com.techteam.fabric.bettermod.impl.block.entity.RoomControllerBlockEntity;
import com.techteam.fabric.bettermod.impl.network.BoxUpdatePayload;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class RoomControllerScreenHandler extends SyncedGuiDescription {
	public final WBoundSlider[] SLIDERS = new WBoundSlider[8];
	public final BlockPos pos;
	public BlockState state;
	public byte minX;
	public byte minY;
	public byte minZ;
	public byte maxX;
	public byte maxY;
	public byte maxZ;
	private Property<?> selectedProperty;

	public RoomControllerScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, RoomControllerBlockEntity blockEntity) {
		this(syncId, playerInventory, blockEntity, new BoxUpdatePayload(
				blockEntity.getPos(),
				BoxUpdatePayload.Vec3b.ORIGIN,
				BoxUpdatePayload.Vec3b.ORIGIN,
				Blocks.AIR.getDefaultState()
		));
	}

	public RoomControllerScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, BoxUpdatePayload payload) {
		this(syncId, playerInventory, new SimpleInventory(1) {
			@Override
			public int getMaxCountPerStack() {
				return 1;
			}
		}, payload);
	}

	public RoomControllerScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, Inventory inventory, BoxUpdatePayload payload) {
		super(BetterMod.ROOM_CONTROLLER_SCREEN_HANDLER_TYPE, syncId, playerInventory, inventory, null);
		pos = payload.pos();
		minX = payload.min().x();
		minY = payload.min().y();
		minZ = payload.min().z();
		maxX = payload.max().x();
		maxY = payload.max().y();
		maxZ = payload.max().z();
		state = payload.state();
		WPlainPanel root = new WPlainPanel();
		root.setInsets(new Insets(2, 7, 0, 7));
		setRootPanel(root);
		setTitleVisible(false);
		root.setSize(176, 168 + 9 + 9);
		{
			WBoundSlider slider = SLIDERS[0] = new WBoundSlider(-63, 0, Axis.HORIZONTAL) {
				@Override
				public void addTooltip(@NotNull TooltipBuilder tooltip) {
					tooltip.add(Text.of("X-" + -value));
				}
			};
			slider.setValue(minX);
			slider.setValueChangeListener(value -> {
				minX = (byte) value;
				RoomControllerScreenHandler.this.sync();
			});
			root.add(slider, 0, 0, 77, 18);
		}
		{
			WBoundSlider slider = SLIDERS[1] = new WBoundSlider(1, 64, Axis.HORIZONTAL) {
				@Override
				public void addTooltip(@NotNull TooltipBuilder tooltip) {
					tooltip.add(Text.of("X+" + (value - 1)));
				}
			};
			slider.setValue(maxX);
			slider.setValueChangeListener((value) -> {
				maxX = (byte) value;
				RoomControllerScreenHandler.this.sync();
			});
			root.add(slider, 85, 0, 77, 18);
		}
		{
			WBoundSlider slider = SLIDERS[2] = new WBoundSlider(-63, 0, Axis.HORIZONTAL) {
				@Override
				public void addTooltip(@NotNull TooltipBuilder tooltip) {
					tooltip.add(Text.of("Y-" + -value));
				}
			};
			slider.setValue(minY);
			slider.setValueChangeListener((value) -> {
				minY = (byte) value;
				RoomControllerScreenHandler.this.sync();
			});
			root.add(slider, 0, 18, 68, 18);
		}
		{
			WBoundSlider slider = SLIDERS[3] = new WBoundSlider(1, 64, Axis.HORIZONTAL) {
				@Override
				public void addTooltip(@NotNull TooltipBuilder tooltip) {
					tooltip.add(Text.of("Y+" + (value - 1)));
				}
			};
			slider.setValue(maxY);
			slider.setValueChangeListener((value) -> {
				maxY = (byte) value;
				RoomControllerScreenHandler.this.sync();
			});
			root.add(slider, 94, 18, 68, 18);
		}
		{
			WBoundSlider slider = SLIDERS[4] = new WBoundSlider(-63, 0, Axis.HORIZONTAL) {
				@Override
				public void addTooltip(@NotNull TooltipBuilder tooltip) {
					tooltip.add(Text.of("Z-" + -value));
				}
			};
			slider.setValue(minZ);
			slider.setValueChangeListener((value) -> {
				minZ = (byte) value;
				RoomControllerScreenHandler.this.sync();
			});
			root.add(slider, 0, 36, 77, 18);
		}
		{
			WBoundSlider slider = SLIDERS[5] = new WBoundSlider(1, 64, Axis.HORIZONTAL) {
				@Override
				public void addTooltip(@NotNull TooltipBuilder tooltip) {
					tooltip.add(Text.of("Z+" + (value - 1)));
				}
			};
			slider.setValue(maxZ);
			slider.setValueChangeListener((value) -> {
				maxZ = (byte) value;
				RoomControllerScreenHandler.this.sync();
			});
			root.add(slider, 85, 36, 77, 18);
		}
		WItemSlot slot = WItemSlot.of(blockInventory, 0);
		root.add(slot, 72, 18);
		slot.setInputFilter((item) -> {
			Block b = Block.getBlockFromItem(item.getItem());
			return b != Blocks.AIR && !(b instanceof BlockEntityProvider);
		});
		root.add(this.createPlayerInventoryPanel(), 0, 72 + 18);
		WBoundSlider slider = SLIDERS[6] = new WBoundSlider(0, 1, Axis.HORIZONTAL) {
			@Override
			public void addTooltip(@NotNull TooltipBuilder tooltip) {
				if (selectedProperty == null) {
					tooltip.add(Text.of("property=null"));
				} else {
					tooltip.add(Text.of("property=" + selectedProperty.getName()));
				}
			}
		};
		WBoundSlider slider2 = SLIDERS[7] = new WBoundSlider(0, 1, Axis.HORIZONTAL) {
			@Override
			public void addTooltip(@NotNull TooltipBuilder tooltip) {
				if (selectedProperty == null) {
					tooltip.add(Text.of("null=null"));
				} else {
					tooltip.add(Text.of(selectedProperty.getName() + "=" + getValueString(state, selectedProperty)));
				}
			}
		};
		slider.setValueChangeListener((value) -> {
			selectedProperty = Iterables.get(state.getBlock().getStateManager().getProperties(), value, null);
			if (selectedProperty == null) {
				slider2.setMaxValue(0);
				return;
			}
			slider2.setMaxValue(selectedProperty.getValues().size() - 1);
			slider2.setValue(RoomControllerScreenHandler.value(state, selectedProperty));
		});
		slider2.setValueChangeListener((value) -> {
			if (selectedProperty != null) {
				state = RoomControllerScreenHandler.with(state, selectedProperty, value);
				RoomControllerScreenHandler.this.sync();
			}
		});
		{
			var properties = state.getBlock().getStateManager().getProperties();
			if (properties.isEmpty()) {
				selectedProperty = null;
				slider.setValues(0, 0, 0);
				slider2.setValues(0, 0, 0);
			} else {
				selectedProperty = Iterables.get(properties, 0);
				slider.setValues(0, properties.size() - 1, 0);
				slider2.setValues(
						0,
						selectedProperty.getValues().size() - 1,
						RoomControllerScreenHandler.value(state, selectedProperty)
				);

			}
		}

		slot.addChangeListener((__, ignoredInventory, index, stack) -> {
			if (!state.isOf(Block.getBlockFromItem(stack.getItem()))) {
				state = Block.getBlockFromItem(stack.getItem()).getDefaultState();
				var properties = state.getBlock().getStateManager().getProperties();
				if (properties.isEmpty()) {
					selectedProperty = null;
					SLIDERS[6].setValues(0, 0, 0);
					SLIDERS[7].setValues(0, 0, 0);
				} else {
					selectedProperty = Iterables.get(properties, 0);
					SLIDERS[6].setValues(0, properties.size() - 1, 0);
					SLIDERS[7].setValues(
							0,
							selectedProperty.getValues().size() - 1,
							RoomControllerScreenHandler.value(state, selectedProperty)
					);
				}
				RoomControllerScreenHandler.this.sync();
			}
			ignoredInventory.markDirty();
		});
		root.add(slider, 0, 54, 176 - 14, 18);
		root.add(slider2, 0, 54 + 18, 176 - 14, 18);
		root.validate(this);

	}

	private static <T extends Comparable<T>> String getValueString(BlockState state, Property<T> property) {
		return property.name(state.get(property));
	}

	private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, int index) {
		return state.with(property, Iterables.get(property.getValues(), index));
	}

	private static <T extends Comparable<T>> int value(BlockState state, Property<T> property) {
		return Iterables.indexOf(property.getValues(), state.get(property)::equals);
	}

	@Override
	public void setProperty(int id, int value) {
		super.setProperty(id, value);
		this.sendContentUpdates();
	}

	public void sync() {
		if (world.isClient()) {
			ClientPlayNetworking.send(new BoxUpdatePayload(
					pos,
					new BoxUpdatePayload.Vec3b(minX, minY, minZ),
					new BoxUpdatePayload.Vec3b(maxX, maxY, maxZ),
					state
			));
		}
	}

	public void update(BoxUpdatePayload payload) {
		SLIDERS[0].setValue(minX = payload.min().x());
		SLIDERS[1].setValue(maxX = payload.max().x());
		SLIDERS[2].setValue(minY = payload.min().y());
		SLIDERS[3].setValue(maxY = payload.max().y());
		SLIDERS[4].setValue(minZ = payload.min().z());
		SLIDERS[5].setValue(maxZ = payload.max().z());
		if (payload.state().isOf(state.getBlock())) {
			BetterMod.LOGGER.info("{} {}", payload.state(), state);
			state = payload.state();
			if (selectedProperty != null) {
				SLIDERS[7].setValue(RoomControllerScreenHandler.value(payload.state(), selectedProperty));
			}
		} else {
			state = payload.state();
			var properties = state.getBlock().getStateManager().getProperties();
			if (properties.isEmpty()) {
				selectedProperty = null;
				SLIDERS[6].setValues(0, 0, 0);
				SLIDERS[7].setValues(0, 0, 0);
			} else {
				selectedProperty = Iterables.get(properties, 0);
				SLIDERS[6].setValues(0, properties.size() - 1, 0);
				SLIDERS[7].setValues(
						0,
						selectedProperty.getValues().size() - 1,
						RoomControllerScreenHandler.value(payload.state(), selectedProperty)
				);
			}
		}
		state = payload.state();
	}
}
