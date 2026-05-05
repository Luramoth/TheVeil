package com.luramoth.theveil.events;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.worldgen.TheVeilModDimensions;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class VeilDimensionHandler {
    private static final Random RANDOM = new Random();

    public static void init() {
        TickEvent.PLAYER_POST.register(player -> {
            if (player.level().dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)){
                simulateVeilfluid(player);

                if (player.level().isClientSide) {
                    spawnAmbientParticles(player);
                }
            }
        });
    }

    private static void simulateVeilfluid(Player player) {
        player.setSwimming(true);


        Vec3 velocity = player.getDeltaMovement();

        double drag = 0.9;
        double buoyancy = 0.01;

        player.setDeltaMovement(
                velocity.x * drag,
                velocity.y * drag + buoyancy,
                velocity.z * drag
        );

        player.setAirSupply(player.getMaxAirSupply());
    }

    private static void spawnAmbientParticles(Player player) {
        for (int i =0; i < 3; i++) {
            double x = player.getX() + (RANDOM.nextDouble() -0.5) * 16;
            double y = player.getY() + (RANDOM.nextDouble() -0.5) * 16;
            double z = player.getZ() + (RANDOM.nextDouble() -0.5) * 16;

            player.level().addParticle(ParticleTypes.UNDERWATER, x, y, z, 0, 0, 0);
        }
    }
}
