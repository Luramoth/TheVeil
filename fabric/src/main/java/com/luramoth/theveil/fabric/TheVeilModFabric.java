package com.luramoth.theveil.fabric;

import net.fabricmc.api.ModInitializer;

import com.luramoth.theveil.TheVeilMod;

public final class TheVeilModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        TheVeilMod.init();
    }
}
