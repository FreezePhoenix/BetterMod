package com.techteam.fabric.bettermod.impl.util;

import org.jetbrains.annotations.Contract;

public class MathUtil {
	@Contract(pure = true)
	public static int div255(int value) {
		return (value + 1 + ((value) >>> 8)) >>> 8;
	}
}
