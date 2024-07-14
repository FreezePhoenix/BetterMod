package com.techteam.fabric.bettermod.impl.client.gui;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.WAbstractSlider;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class WBoundSlider extends WSlider {

	public WBoundSlider(int min, int max, Axis axis) {
		super(min, max, axis);
	}

	public void setValues(int min, int max, int value) {
		this.min = min;
		this.max = max;
		this.value = value;
		updateValueCoordRatios();
	}
}
