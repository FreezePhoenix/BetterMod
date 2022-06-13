package com.techteam.fabric.bettermod.mixin.intrinsics;

import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public abstract class Matrix4fMixin {
	@Shadow
	protected float a00;
	@Shadow
	protected float a01;
	@Shadow
	protected float a02;
	@Shadow
	protected float a03;
	@Shadow
	protected float a10;
	@Shadow
	protected float a11;
	@Shadow
	protected float a12;
	@Shadow
	protected float a13;
	@Shadow
	protected float a20;
	@Shadow
	protected float a21;
	@Shadow
	protected float a22;
	@Shadow
	protected float a23;
	@Shadow
	protected float a30;
	@Shadow
	protected float a31;
	@Shadow
	protected float a32;
	@Shadow
	protected float a33;

	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public void multiplyByTranslation(final float x, final float y, final float z) {
		this.a03 = Math.fma(this.a02, z, Math.fma(this.a01, y, Math.fma(this.a00, x, this.a03)));
		this.a13 = Math.fma(this.a12, z, Math.fma(this.a11, y, Math.fma(this.a10, x, this.a13)));
		this.a23 = Math.fma(this.a22, z, Math.fma(this.a21, y, Math.fma(this.a20, x, this.a23)));
		this.a33 = Math.fma(this.a32, z, Math.fma(this.a31, y, Math.fma(this.a30, x, this.a33)));
	}
}
