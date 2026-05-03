package com.luramoth.theveil.neoforge.client;

import com.luramoth.theveil.client.TheVeilModClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;

@Mod(value = "the_veil", dist = Dist.CLIENT)
public class TheVeilModNeoForgeClient {
    public TheVeilModNeoForgeClient(FMLModContainer container, IEventBus modBus, Dist dist) {
        TheVeilModClient.init();
    }
}
