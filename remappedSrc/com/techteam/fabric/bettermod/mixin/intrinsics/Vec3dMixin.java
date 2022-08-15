package com.techteam.fabric.bettermod.mixin.intrinsics;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3d.class)
public abstract class Vec3dMixin {
	@Shadow
	@Final
	public double x;

	@Shadow
	@Final
	public double y;

	@Shadow
	@Final
	public double z;

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public double dotProduct(Vec3d other) {
		return Math.fma(
				this.z,
				other.z,
				Math.fma(
						this.y,
						other.y,
						this.x * other.x
				)
		);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public double lengthSquared() {
		return Math.fma(this.z, this.z, Math.fma(this.y, this.y, this.x * this.x));
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public double horizontalLengthSquared() {
		return Math.fma(this.z, this.z, this.x * this.x);
	}
}
