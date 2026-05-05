package com.luramoth.theveil.events;

import com.luramoth.theveil.worldgen.TheVeilModDimensions;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class VeilDimensionHandler {
    public static void init() {
        TickEvent.PLAYER_POST.register(player -> {
            if (player.level().dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)){
                simulateVeilfluid(player);
            }
        });
    }

    private static void simulateVeilfluid(Player player) {
//        player.setSwimming(true);
//
//
//        Vec3 velocity = player.getDeltaMovement();
//
//        double drag = 0.5;
//
//        player.setDeltaMovement(
//                velocity.x * drag,
//                velocity.y * drag,
//                velocity.z * drag
//        );
//
//        player.setAirSupply(player.getMaxAirSupply());
    }
}
