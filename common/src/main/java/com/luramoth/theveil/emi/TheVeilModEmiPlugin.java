package com.luramoth.theveil.emi;

import com.luramoth.theveil.data.CatalystManager;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

@EmiEntrypoint
public class TheVeilModEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
        CatalystManager.CATALYSTS.forEach((item, dim) -> {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            emiRegistry.addRecipe(new PendantEmiRecipe(itemId, dim));
        });
    }
}
