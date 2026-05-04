package com.luramoth.theveil.client;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.TheVeilModKeybinds;
import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.items.TheVeilModItems;
import com.luramoth.theveil.networking.PendantAction;
import com.luramoth.theveil.networking.TheVeilModNetworking;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.resources.ResourceLocation;

public class TheVeilModClient {

    public static void init(){
        // keybinds
        TheVeilModKeybinds.init();

        ClientTickEvent.CLIENT_LEVEL_POST.register(level -> {
            while (TheVeilModKeybinds.NEXT_DIMENSION.consumeClick()){
                TheVeilModNetworking.sendAction(PendantAction.NEXT);
            }
            while (TheVeilModKeybinds.LAST_DIMENSION.consumeClick()){
                TheVeilModNetworking.sendAction(PendantAction.LAST);
            }
            while (TheVeilModKeybinds.OPEN_RIFT.consumeClick()){
                TheVeilModNetworking.sendAction(PendantAction.OPEN_RIFT);
            }
        });

        // Pendant appearence
        ItemPropertiesRegistry.register(TheVeilModItems.PENDANT.get(),
                ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, "has_gem"),
                (stack, level, entity, seed) -> {
                    PendantData data = stack.get(TheVeilModComponents.PENDENT_DATA.get());
                    return (data != null && !data.unlockedDimensions().isEmpty()) ? 1.0f : 0.0f;
                });
    }
}
