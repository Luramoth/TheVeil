package com.luramoth.theveil;

import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.data.CatalystManager;
import com.luramoth.theveil.fluids.TheVeilModFluids;
import com.luramoth.theveil.items.TheVeilModItems;
import com.luramoth.theveil.networking.TheVeilModNetworking;
import com.luramoth.theveil.recipies.TheVeilModRecipes;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TheVeilMod {
    public static final String MOD_ID = "the_veil";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Mod loader is entering The Veil...");

        TheVeilModFluids.FLUIDS.register();
        TheVeilModFluids.BLOCKS.register();
        TheVeilModItems.ITEMS.register();
        TheVeilModComponents.COMPONENTS.register();
        TheVeilModRecipes.SERIALISERS.register();

        TheVeilModNetworking.init();

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new CatalystManager());
    }
}
