package com.techteam.fabric.bettermod.impl.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
	private static final String MIXIN_PACKAGE_ROOT = "com.techteam.fabric.bettermod.mixin";
	private static final String MIXIN_PACKAGE_ROOT_LITHIUM = "com.techteam.fabric.bettermod.mixin.lithium.";

	private final Logger logger = LogManager.getLogger("BetterMod");
	private boolean LITHIUM_PRESENT = false;

	@Override
	public void onLoad(String mixinPackage) {
		if(FabricLoader.getInstance().isModLoaded("lithium")) {
			logger.info("BetterMod has detected Lithium. Lithium will be used to improve performance.");
			LITHIUM_PRESENT = true;
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if(mixinClassName.startsWith(MIXIN_PACKAGE_ROOT_LITHIUM)) {
			return LITHIUM_PRESENT;
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
}
