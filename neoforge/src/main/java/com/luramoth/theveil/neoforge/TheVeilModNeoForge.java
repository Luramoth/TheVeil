package com.luramoth.theveil.neoforge;

import net.neoforged.fml.common.Mod;

import com.luramoth.theveil.TheVeilMod;

@Mod(TheVeilMod.MOD_ID)
public final class TheVeilModNeoForge {
    public TheVeilModNeoForge() {
        // Run our common setup.
        TheVeilMod.init();
    }
}
