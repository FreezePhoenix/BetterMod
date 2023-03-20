package com.techteam.fabric.bettermod.util;

import org.jetbrains.annotations.Contract;
import org.joml.Matrix3f;
import sun.misc.Unsafe;

public class MathUtil {
	@Contract(pure = true)
	public static int div255(int value) {
		return (value + 1 + ((value) >>> 8)) >>> 8;
	}
}
