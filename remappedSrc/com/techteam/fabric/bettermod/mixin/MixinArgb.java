package com.techteam.fabric.bettermod.impl.mixin;

import com.techteam.fabric.bettermod.impl.util.MathUtil;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ColorHelper.Argb.class)
public abstract class MixinArgb {
	/**
	 * @author Aria
	 * @reason Use knowledge about values to improve performance
	 */
	@Overwrite
	public static int mixColor(int first, int second) {
		return ColorHelper.Argb.getArgb(
				MathUtil.div255(ColorHelper.Argb.getAlpha(first) * ColorHelper.Argb.getAlpha(second)),
				MathUtil.div255(ColorHelper.Argb.getRed(first) * ColorHelper.Argb.getRed(second)),
				MathUtil.div255(ColorHelper.Argb.getGreen(first) * ColorHelper.Argb.getGreen(second)),
				MathUtil.div255(ColorHelper.Argb.getBlue(first) * ColorHelper.Argb.getBlue(second))
		);
	}
}
