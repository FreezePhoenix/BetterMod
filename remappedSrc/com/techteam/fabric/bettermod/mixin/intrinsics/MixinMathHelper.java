package com.techteam.fabric.bettermod.mixin.intrinsics;

import com.ibm.icu.util.CodePointTrie;
import com.techteam.fabric.bettermod.util.MathUtil;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MathHelper.class)
public abstract class MixinMathHelper {
	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Contract(pure = true)
	@Overwrite
	public static double squaredMagnitude(double x, double y, double z) {
		/*
		But Aria! Why not do Math.fma(x, x, Math.fma(y, y, z * z))?
		Well, that generates the following (assuming X is R0, Y is R1, Z is R2, a temporary is R3,
		and the final result is stored in R0

			R3 = R0
			R0 = R2 * R2
			R0 = R0 + R1 * R1
			R0 = R0 + R3 * R3

		While Math.fma(z, z, Math.fma(y, y, x * x)) yields:

			R0 = R0 * R0
			R0 = R0 + R1 * R1
			R0 = R0 + R2 * R2

		As you can see, no temporary.
		 */
		return Math.fma(z, z, Math.fma(y, y, x * x));
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Contract(pure = true)
	@Overwrite
	public static double squaredHypot(double x, double y) {
		return Math.fma(y, y, x * x);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static float lerp(float delta, float start, float end) {
		return Math.fma(end - start, delta, start);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static double lerp(double delta, double start, double end) {
		return Math.fma(end - start, delta, start);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static int floor(float value) {
		return (int) Math.floor(value);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Contract(pure = true)
	@Overwrite
	public static int floor(double value) {
		return (int) Math.floor(value);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Contract(pure = true)
	@Overwrite
	public static int ceil(float value) {
		return (int) Math.ceil(value);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Contract(pure = true)
	@Overwrite
	public static int ceil(double value) {
		return (int) Math.ceil(value);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static int clamp(int value, int min, int max) {
		if(value < min) {
			return min;
		}
		if(value > max) {
			return max;
		}
		return value;
	}


	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static float clamp(float value, float min, float max) {
		if(value < min) {
			return min;
		}
		if(value > max) {
			return max;
		}
		return value;
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static double clamp(double value, double min, double max) {
		if(value < min) {
			return min;
		}
		if(value > max) {
			return max;
		}
		return value;
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static float stepTowards(float from, float to, float step) {
		return MathHelper.clamp(from + Math.copySign(step, to - from), Math.min(from, to), Math.max(from, to));
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static float clampedLerp(float start, float end, float delta) {
		return MathHelper.lerp(MathHelper.clamp(delta, -1.0f, 1.0f), start, end);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static double clampedLerp(double start, double end, double delta) {
		return MathHelper.lerp(MathHelper.clamp(delta, -1.0d, 1.0d), start, end);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics, avoid unneeded casts
	 */
	@Contract(pure = true)
	@Overwrite
	public static double fractionalPart(double value) {
		return value - Math.floor(value);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics, avoid unneeded casts
	 */
	@Contract(pure = true)
	@Overwrite
	public static float fractionalPart(float value) {
		return value - (float) Math.floor(value);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Contract(pure = true)
	@Overwrite
	public static int sign(double value) {
		return (int) Math.signum(value);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Contract(pure = true)
	@Overwrite
	public static int ceilLog2(int value) {
		if(value == 0) {
			return 0;
		}
		return 32 - Integer.numberOfLeadingZeros(value - 1);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Contract(pure = true)
	@Overwrite
	public static int floorLog2(int value) {
		return 31 - Integer.numberOfLeadingZeros(value);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Contract(pure = true)
	@Overwrite
	public static long lfloor(double value) {
		return (long) Math.floor(value);
	}
}
