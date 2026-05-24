package com.luramoth.theveil.entities;

import com.luramoth.theveil.TheVeilMod;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class TheVeilModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(TheVeilMod.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<RiftEntity>> RIFT = ENTITIES.register("rift",
            ()-> EntityType.Builder.of(RiftEntity::new, MobCategory.MISC)
                    .sized(2.0f, 3.0f)
                    .build("rift")
    );
}
