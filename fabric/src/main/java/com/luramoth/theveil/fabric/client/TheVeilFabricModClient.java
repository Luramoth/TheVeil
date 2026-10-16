package com.luramoth.theveil.fabric.client;

import com.luramoth.theveil.client.PendantGemColor;
import com.luramoth.theveil.client.TheVeilModClient;
import com.luramoth.theveil.items.PendentItem;
import com.luramoth.theveil.items.TheVeilModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public final class TheVeilFabricModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TheVeilModClient.initRegistrations();
        TheVeilModClient.init();

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? PendantGemColor.get(stack) : -1,
                TheVeilModItems.PENDANT.get());
    }
}
