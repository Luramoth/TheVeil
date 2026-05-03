package com.luramoth.theveil.fabric.client;

import com.luramoth.theveil.client.TheVeilModClient;
import net.fabricmc.api.ClientModInitializer;

public final class TheVeilFabricModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TheVeilModClient.init();
    }
}
