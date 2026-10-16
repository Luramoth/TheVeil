package com.luramoth.theveil.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.recipies.PendantAttunementRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalystManager {
    public static final int FALLBACK_COLOR = 0xFF130134;

    private CatalystManager() {}

    @SuppressWarnings("unchecked")
    public static List<RecipeHolder<PendantAttunementRecipe>> all(RecipeManager recipes) {
        List<RecipeHolder<PendantAttunementRecipe>> out = new ArrayList<>();
        for (RecipeHolder<SmithingRecipe> holder : recipes.getAllRecipesFor(RecipeType.SMITHING)) {
            if (holder.value() instanceof  PendantAttunementRecipe) {
                out.add((RecipeHolder<PendantAttunementRecipe>) (RecipeHolder<?>) holder);
            }
        }
        return out;
    }

    public static int getColorFor(RecipeManager recipes, ResourceKey<Level> dimension) {
        for (RecipeHolder<SmithingRecipe> holder : recipes.getAllRecipesFor(RecipeType.SMITHING)) {
            if (holder.value() instanceof PendantAttunementRecipe recipe && recipe.dimension().equals(dimension)) {
                return recipe.color();
            }
        }
        return FALLBACK_COLOR;
    }
}
