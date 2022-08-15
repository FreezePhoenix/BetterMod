package com.techteam.fabric.bettermod.client.gui;

import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

public class WBoundSlider extends WSlider {
	final int bound_index;

	public WBoundSlider(int min, int max, Axis axis, int bound_index) {
		super(min, max, axis);
		this.bound_index = bound_index;
	}

	public WBoundSlider(int min, int max, boolean axis, int bound_index) {
		super(
				min,
				max,
				axis
						? Axis.HORIZONTAL
						: Axis.VERTICAL
		);
		this.bound_index = bound_index;
	}
}
