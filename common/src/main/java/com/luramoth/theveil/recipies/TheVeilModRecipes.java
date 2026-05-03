package com.luramoth.theveil.recipies;

import com.luramoth.theveil.TheVeilMod;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class TheVeilModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALISERS =
            DeferredRegister.create(TheVeilMod.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeSerializer<PendantSmithingRecipe>> PENDANT_SMITHING =
            SERIALISERS.register("pendant_smithing", PendantSmithingRecipe.Serialiser::new);
}
