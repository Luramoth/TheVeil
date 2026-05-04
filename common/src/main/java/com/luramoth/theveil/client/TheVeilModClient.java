package com.luramoth.theveil.client;

import com.luramoth.theveil.TheVeilModKeybinds;
import com.luramoth.theveil.networking.PendantAction;
import com.luramoth.theveil.networking.TheVeilModNetworking;
import dev.architectury.event.events.client.ClientTickEvent;

public class TheVeilModClient {

    public static void init(){
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
    }
}
