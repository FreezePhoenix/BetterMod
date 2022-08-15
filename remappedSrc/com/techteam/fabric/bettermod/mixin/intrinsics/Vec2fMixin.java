package com.techteam.fabric.bettermod.mixin.intrinsics;

import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec2f.class)
public abstract class Vec2fMixin {
	@Shadow @Final public float x;

	@Shadow @Final public float y;
	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public float dot(Vec2f vec) {
		return Math.fma(this.y, vec.y, this.x * vec.x);
	}
	/**
	 * @author Aria
	 * @reason Use Intrinsics
	 */
	@Overwrite
	public float lengthSquared() {
		return Math.fma(this.y, this.y, this.x * this.x);
	}
}
