package com.luramoth.theveil.fabric.client;

import com.luramoth.theveil.client.TheVeilModClient;
import com.luramoth.theveil.items.PendentItem;
import com.luramoth.theveil.items.TheVeilModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public final class TheVeilFabricModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TheVeilModClient.init();

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? PendentItem.getGemColor(stack) : -1,
                TheVeilModItems.PENDANT.get());
    }
}
