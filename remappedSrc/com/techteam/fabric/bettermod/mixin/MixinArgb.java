package com.techteam.fabric.bettermod.mixin;

import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ColorHelper.Argb.class)
public abstract class MixinArgb {
	@Contract(pure = true)
	private static int div255(int value) {
		return (value + 1 + ((value) >>> 8)) >>> 8;
	}

	/**
	 * @author Aria
	 * @reason Use knowledge about values to improve performance
	 */
	@Overwrite
	public static int mixColor(int first, int second) {
		return ColorHelper.Argb.getArgb(
				div255(ColorHelper.Argb.getAlpha(first) * ColorHelper.Argb.getAlpha(second)),
				div255(ColorHelper.Argb.getRed(first) * ColorHelper.Argb.getRed(second)),
				div255(ColorHelper.Argb.getGreen(first) * ColorHelper.Argb.getGreen(second)),
				div255(ColorHelper.Argb.getBlue(first) * ColorHelper.Argb.getBlue(second))
		);
	}
}
