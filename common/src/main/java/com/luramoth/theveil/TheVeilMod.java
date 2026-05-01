package com.luramoth.theveil;

import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.items.TheVeilModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TheVeilMod {
    public static final String MOD_ID = "the_veil";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Mod loader is entering The Veil...");

        TheVeilModItems.ITEMS.register();
        TheVeilModComponents.COMPONENTS.register();
    }
}
