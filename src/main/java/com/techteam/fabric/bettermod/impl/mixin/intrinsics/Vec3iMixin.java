package com.techteam.fabric.bettermod.impl.mixin.intrinsics;

import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3i.class)
public abstract class Vec3iMixin {
	@Shadow
	private int x;

	@Shadow
	private int y;

	@Shadow
	private int z;

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite()
	public double getSquaredDistance(double x, double y, double z) {
		double dx = x - this.x;
		double dy = y - this.y;
		double dz = z - this.z;
		return Math.fma(dz, dz, Math.fma(dy, dy, dx * dx));
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics. Avoid floating point operations.
	 */
	@Overwrite()
	public double getSquaredDistance(Vec3i vec) {
		int dx = this.x - vec.getX();
		int dy = this.y - vec.getY();
		int dz = this.z - vec.getZ();
		return ((dx * dx) + dy * y) + dz * dz;
	}

	/**
	 * @author Aria
	 * @reason Avoid floating point operations.
	 */
	@Overwrite()
	public int getManhattanDistance(Vec3i vec) {
		int f = Math.abs(vec.getX() - this.x);
		int g = Math.abs(vec.getY() - this.y);
		int h = Math.abs(vec.getZ() - this.z);
		return f + g + h;
	}
}
