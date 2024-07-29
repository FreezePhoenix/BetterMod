package com.techteam.fabric.bettermod.impl.client.gui;

import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

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
