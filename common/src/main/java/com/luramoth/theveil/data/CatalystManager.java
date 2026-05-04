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
import java.util.List;
import java.util.Map;

public class CatalystManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final Map<Item, CatalystData> CATALYSTS = new HashMap<>();

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

                // get color
                String hexString = json.get("color").getAsString();
                int color = Integer.parseInt(hexString, 16);

                CATALYSTS.put(item, new CatalystData(dimKey, color));
            } catch (Exception e) {
                TheVeilMod.LOGGER.error("Error loading catalysts: {}", String.valueOf(e));
            }
        });

        TheVeilMod.LOGGER.info("Loaded {} veil catalysts.", CATALYSTS.size());
        CATALYSTS.forEach((item, catalystData) ->
                TheVeilMod.LOGGER.info("Catalyst: {} -> {}", item, catalystData.dim().location())
        );
    }

    public static ResourceKey<Level> getDimensionFor(Item item) {
        return CATALYSTS.get(item).dim();
    }

    public static int getColorFor(ResourceKey<Level> dimension) {
        List<CatalystData> values = CATALYSTS.values().stream().toList();

        if (values.isEmpty()) return 0x130134;

        for (int i = 0; i < values.size() - 1; i++) {
            if (values.get(i).dim() == dimension) return values.get(i).color();
        }

        return 0x130134;
    }
}
