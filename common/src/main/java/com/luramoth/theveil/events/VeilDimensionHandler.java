package com.luramoth.theveil.events;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.mixin.LivingEntityAccessor;
import com.luramoth.theveil.worldgen.TheVeilModDimensions;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class VeilDimensionHandler {
    private static final Random RANDOM = new Random();

    public static final TagKey<Item> VEIL_RESISTANT = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, "veil_resistant"));

    public static void init() {
        TickEvent.PLAYER_POST.register(player -> {
            if (player.level().dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)){
                simulateVeilfluid(player);

                if (player.level().isClientSide) {
                    spawnAmbientParticles(player);
                } else {
                    dissolveEquipment(player);
                }
            }
        });
    }

    private static void dissolveEquipment(Player player) {
        //if (player.isCreative() || player.isSpectator()) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            EquipmentSlot.Type type = slot.getType();
            if (type != EquipmentSlot.Type.HAND && type != EquipmentSlot.Type.HUMANOID_ARMOR) continue;

            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || stack.is(VEIL_RESISTANT)) continue;

            player.setItemSlot(slot, ItemStack.EMPTY);
            playDissolveEffects(level, player, slot, stack);
        }
    }

    private static void playDissolveEffects(ServerLevel level, Player player, EquipmentSlot slot, ItemStack stack) {
        double y = switch (slot) {
            case HEAD -> player.getEyeY();
            case LEGS -> player.getY() + player.getBbHeight() * 0.35;
            case FEET -> player.getY() + 0.1;
            default -> player.getY() + player.getBbHeight() * 0.6;
        };

        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack),
                player.getX(), y, player.getZ(), 12, 0.25, 0.15, 0.25, 0.02);

        level.playSound(null, player.getX(), y, player.getZ(),
                SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.8f, 0.6f * RANDOM.nextFloat() * 0.2f);
        level.playSound(null, player.getX(), y, player.getZ(),
                SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 0.4f, 0.5f);
    }

    private static void simulateVeilfluid(Player player) {
        player.setSwimming(true);


        Vec3 velocity = player.getDeltaMovement();

        double drag = 0.9;
        double buoyancy = 0.01;

        boolean isJumping = ((LivingEntityAccessor) player).isJumping();

        double lift = ((LivingEntityAccessor) player).isJumping() ? 0.08 : 0;

        player.setDeltaMovement(
                velocity.x * drag,
                velocity.y * drag + buoyancy + lift,
                velocity.z * drag
        );

        player.setAirSupply(player.getMaxAirSupply());
    }

    private static void spawnAmbientParticles(Player player) {
        for (int i =0; i < 5; i++) {
            double x = player.getX() + (RANDOM.nextDouble() -0.5) * 16;
            double y = player.getY() + (RANDOM.nextDouble() -0.5) * 16;
            double z = player.getZ() + (RANDOM.nextDouble() -0.5) * 16;

            player.level().addParticle(ParticleTypes.UNDERWATER, x, y, z, 0, 0, 0);
        }
    }
}
