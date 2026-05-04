package com.luramoth.theveil.neoforge.client;

import com.luramoth.theveil.client.TheVeilModClient;
import com.luramoth.theveil.items.PendentItem;
import com.luramoth.theveil.items.TheVeilModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@Mod(value = "the_veil", dist = Dist.CLIENT)
public class TheVeilModNeoForgeClient {
    public TheVeilModNeoForgeClient(FMLModContainer container, IEventBus modBus, Dist dist) {
        TheVeilModClient.init();
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 1 ? PendentItem.getGemColor(stack) : -1,
                TheVeilModItems.PENDANT.get());
    }
}
