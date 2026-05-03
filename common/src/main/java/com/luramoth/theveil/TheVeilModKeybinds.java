package com.luramoth.theveil;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;

public class TheVeilModKeybinds {
    public static final KeyMapping NEXT_DIMENSION = new KeyMapping(
            "key.the_veil.next_dimension",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_RBRACKET,
            "category.the_veil.key_category"
    );
    public static final KeyMapping LAST_DIMENSION = new KeyMapping(
            "key.the_veil.last_dimension",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_LBRACKET,
            "category.the_veil.key_category"
    );
    public static final KeyMapping OPEN_RIFT = new KeyMapping(
            "key.the_veil.open_rift",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_V,
            "category.the_veil.key_category"
    );

    public static void init(){
        KeyMappingRegistry.register(NEXT_DIMENSION);
        KeyMappingRegistry.register(LAST_DIMENSION);
        KeyMappingRegistry.register(OPEN_RIFT);
    }
}
