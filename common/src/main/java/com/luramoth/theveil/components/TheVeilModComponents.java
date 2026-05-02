package com.luramoth.theveil.components;

import com.luramoth.theveil.TheVeilMod;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import java.util.function.UnaryOperator;

public class TheVeilModComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(TheVeilMod.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<PendantData>> PENDENT_DATA =
            register("pendant_data", builder -> builder
                    .persistent(PendantData.CODEC)
                    .networkSynchronized(PendantData.STREAM_CODEC)
            );

    private static <T> RegistrySupplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder){
        return COMPONENTS.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
