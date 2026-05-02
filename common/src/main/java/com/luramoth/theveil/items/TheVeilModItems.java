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
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(TheVeilMod.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<PendentItem> PENDANT = registerItem("pendant", () -> new PendentItem(new Item.Properties()
            .stacksTo(1)
            .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
    ));

    public static <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, name), item);
    }
}
