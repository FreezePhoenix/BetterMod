package com.techteam.fabric.bettermod.client.gui;

import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.StairsBlock;

public class WDynamicSlider extends WSlider {

    public WDynamicSlider(int min, int max, Axis axis) {
        super(min, max, axis);
    }

    public WDynamicSlider(int min, int max, boolean axis) {
        super(
                min, max, axis
                        ? Axis.HORIZONTAL
                        : Axis.VERTICAL
        );
    }

    @Override
    public void setMaxValue(int max) {
        super.setMaxValue(max);
        int trackHeight = (this.axis == Axis.HORIZONTAL
                ? this.width
                : this.height) - super.getThumbWidth();
        this.valueToCoordRatio = (float) (this.max - this.min) / trackHeight;
        this.coordToValueRatio = 1 / this.valueToCoordRatio;
    }

    @Override
    public void setMinValue(int min) {
        super.setMinValue(min);
        int trackHeight = (axis == Axis.HORIZONTAL
                ? this.width
                : this.height) - super.getThumbWidth();
        this.valueToCoordRatio = (float) (this.max - this.min) / trackHeight;
        this.coordToValueRatio = 1 / this.valueToCoordRatio;
    }
}
