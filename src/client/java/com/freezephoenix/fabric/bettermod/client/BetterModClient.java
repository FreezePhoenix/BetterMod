package com.freezephoenix.fabric.bettermod.client;

import com.freezephoenix.fabric.FreezeLib;
import com.freezephoenix.fabric.bettermod.BetterMod;
import com.freezephoenix.fabric.client.FreezeLibClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BetterModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FreezeLibClient.registerMenu(BetterMod.BOOKSHELF_SCREEN_HANDLER_TYPE);
	}
}
