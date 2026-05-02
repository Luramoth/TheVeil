package com.luramoth.theveil.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.luramoth.theveil.TheVeilMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class CatalystManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final Map<Item, ResourceKey<Level>> CATALYSTS = new HashMap<>();

    public CatalystManager() {
        super(GSON, "veil_catalysts");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        CATALYSTS.clear();

        object.forEach((location, element) -> {
            try {
                JsonObject json = element.getAsJsonObject();

                // get item
                String itemId = json.get("item").getAsString();
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));

                // get dimension
                String dimId = json.get("dimension").getAsString();
                ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimId));

                CATALYSTS.put(item, dimKey);
            } catch (Exception e) {
                TheVeilMod.LOGGER.error("Error loading catalysts: {e}");
            }
        });

        TheVeilMod.LOGGER.info("Loaded {} veil catalysts.", CATALYSTS.size());
        CATALYSTS.forEach((item, dim) ->
                TheVeilMod.LOGGER.info("Catalyst: {} -> {}", item, dim.location())
        );
    }

    public static ResourceKey<Level> getDimensionFor(Item item) {
        return CATALYSTS.get(item);
    }
}
