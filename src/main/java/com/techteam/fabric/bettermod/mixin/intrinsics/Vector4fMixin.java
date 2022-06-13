package com.techteam.fabric.bettermod.mixin.intrinsics;

import net.minecraft.util.math.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vector4f.class)
public abstract class Vector4fMixin {
	@Shadow
	private float x;
	@Shadow
	private float y;
	@Shadow
	private float z;
	@Shadow
	private float w;

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public float dotProduct(Vector4f other) {
		return Math.fma(this.w, other.getW(), Math.fma(this.z, other.getZ(), Math.fma(this.y, other.getY(), this.x * other.getX())));
	}

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public void lerp(Vector4f to, float delta) {
		this.x = Math.fma(to.getX() - this.x, delta, this.x);
		this.y = Math.fma(to.getY() - this.y, delta, this.y);
		this.z = Math.fma(to.getZ() - this.z, delta, this.z);
		this.w = Math.fma(to.getW() - this.w, delta, this.w);
	}
}
