package com.techteam.fabric.bettermod.mixin.intrinsics;

import net.minecraft.util.math.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix3f.class)
public abstract class Matrix3fMixin {
	@Shadow
	protected float a00;
	@Shadow
	protected float a01;
	@Shadow
	protected float a02;
	@Shadow
	protected float a10;
	@Shadow
	protected float a11;
	@Shadow
	protected float a12;
	@Shadow
	protected float a20;
	@Shadow
	protected float a21;
	@Shadow
	protected float a22;
	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public void multiply(Matrix3f matrix) {
		Matrix3fMixin other = (Matrix3fMixin) (Object) matrix;
		float f = Math.fma(this.a00, other.a00, Math.fma(this.a01, other.a10, this.a02 * other.a20));
		float g = Math.fma(this.a00, other.a01, Math.fma(this.a01, other.a11, this.a02 * other.a21));
		float h = Math.fma(this.a00, other.a02, Math.fma(this.a01, other.a12, this.a02 * other.a22));
		float i = Math.fma(this.a10, other.a00, Math.fma(this.a11, other.a10, this.a12 * other.a20));
		float j = Math.fma(this.a10, other.a01, Math.fma(this.a11, other.a11, this.a12 * other.a21));
		float k = Math.fma(this.a10, other.a02, Math.fma(this.a11, other.a12, this.a12 * other.a22));
		float l = Math.fma(this.a20, other.a00, Math.fma(this.a21, other.a10, this.a22 * other.a20));
		float m = Math.fma(this.a20, other.a01, Math.fma(this.a21, other.a11, this.a22 * other.a21));
		float n = Math.fma(this.a20, other.a02, Math.fma(this.a21, other.a12, this.a22 * other.a22));
		this.a00 = f;
		this.a01 = g;
		this.a02 = h;
		this.a10 = i;
		this.a11 = j;
		this.a12 = k;
		this.a20 = l;
		this.a21 = m;
		this.a22 = n;
	}
}
