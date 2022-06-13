package com.techteam.fabric.bettermod.mixin.intrinsics;

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
	public static int absFloor(double value) {
		return MathHelper.abs(MathHelper.floor(value));
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
	public static byte clamp(byte value, byte min, byte max) {
		return (byte) Math.min(Math.max(value, min), max);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static long clamp(long value, long min, long max) {
		return Math.min(Math.max(value, min), max);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static float clamp(float value, float min, float max) {
		return Math.min(Math.max(value, min), max);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
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
		return MathHelper.clamp(MathHelper.lerp(delta, start, end), start, end);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public static double clampedLerp(double start, double end, double delta) {
		return MathHelper.clamp(MathHelper.lerp(delta, start, end), start, end);
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
	public static int packRgb(int r, int g, int b) {
		return (((r << 8) | g) << 8) | b;
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
