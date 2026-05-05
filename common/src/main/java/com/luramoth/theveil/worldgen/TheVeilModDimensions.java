package com.luramoth.theveil.worldgen;

import com.luramoth.theveil.TheVeilMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class TheVeilModDimensions {
    public static final ResourceKey<Level> THE_VEIL_KEY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, "the_veil")
    );

    public static final ResourceKey<DimensionType> THE_VEIL_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, "the_veil_type")
    );
}
