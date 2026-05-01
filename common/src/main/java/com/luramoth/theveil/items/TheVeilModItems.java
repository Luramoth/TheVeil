package com.luramoth.theveil.items;

import com.luramoth.theveil.TheVeilMod;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class TheVeilModItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(TheVeilMod.MOD_ID, Registries.ITEM);

    public static RegistrySupplier<Item> PENDANT;

    public static RegistrySupplier<Item> registerItem(String name, Supplier<Item> item) {
        return ITEMS.register(ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, name), item);
    }

    public static void init() {
        PENDANT = registerItem("pendant", () -> new Item(new Item.Properties().stacksTo(1).arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)));

        ITEMS.register();
    }
}
