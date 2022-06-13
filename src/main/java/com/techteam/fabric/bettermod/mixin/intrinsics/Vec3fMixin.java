package com.techteam.fabric.bettermod.mixin.intrinsics;

import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3f.class)
public abstract class Vec3fMixin {
	@Shadow
	private float x;
	@Shadow
	private float y;
	@Shadow
	private float z;

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public void lerp(Vec3f to, float delta) {
		this.x = Math.fma(to.getX() - this.x, delta, this.x);
		this.y = Math.fma(to.getY() - this.y, delta, this.y);
		this.z = Math.fma(to.getZ() - this.z, delta, this.z);
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public float dot(Vec3f other) {
		return Math.fma(this.z, other.getZ(), Math.fma(this.y, other.getY(), this.x * other.getX()));
	}
}
