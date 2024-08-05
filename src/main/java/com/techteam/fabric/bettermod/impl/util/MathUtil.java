package com.techteam.fabric.bettermod.impl.util;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Contract;

public class MathUtil {
	@Contract(pure = true)
	public static int div255(int value) {
		return (value + 1 + ((value) >>> 8)) >>> 8;
	}
	@Contract(pure = true)
	public static byte clamp(byte value, byte min, byte max) {
		return (byte) MathHelper.clamp(value, min, max);
	}
}
