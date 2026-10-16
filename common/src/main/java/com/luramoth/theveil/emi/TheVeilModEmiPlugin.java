package com.luramoth.theveil.emi;

import com.luramoth.theveil.data.CatalystManager;
import com.luramoth.theveil.recipies.PendantAttunementRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

@EmiEntrypoint
public class TheVeilModEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry emiRegistry) {
        for (RecipeHolder<PendantAttunementRecipe> holder : CatalystManager.all(emiRegistry.getRecipeManager())) {
            emiRegistry.addRecipe(new PendantEmiRecipe(holder));
        }
    }
}
