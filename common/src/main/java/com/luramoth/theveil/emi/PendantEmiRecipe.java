package com.luramoth.theveil.emi;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.items.TheVeilModItems;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PendantEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiIngredient catalyst;
    private final EmiStack output;

    public PendantEmiRecipe(ResourceLocation catalystId, ResourceKey<Level> dim) {
        // make a unique ID for this EMI entry
        this.id = ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID,
                "/pendant_smithing/" + catalystId.getNamespace() + "/" + catalystId.getPath());

        this.catalyst = EmiIngredient.of(List.of(EmiStack.of(BuiltInRegistries.ITEM.get(catalystId))));

        ItemStack resultStack = new ItemStack(TheVeilModItems.PENDANT.get());
        resultStack.set(TheVeilModComponents.PENDENT_DATA.get(), new PendantData(List.of(dim)));
        this.output = EmiStack.of(resultStack);
    }

    @Override
    public EmiRecipeCategory getCategory() { return VanillaEmiRecipeCategories.SMITHING; }

    @Override
    public @Nullable ResourceLocation getId() { return id;}

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiStack.EMPTY, EmiStack.of(TheVeilModItems.PENDANT.get()), catalyst);
    }

    @Override
    public List<EmiStack> getOutputs() { return List.of(output); }

    @Override
    public int getDisplayWidth() { return 112; }

    @Override
    public int getDisplayHeight() { return 18; }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        widgetHolder.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);

        widgetHolder.addSlot(EmiStack.EMPTY, 0,0);
        widgetHolder.addSlot(EmiStack.of(TheVeilModItems.PENDANT.get()), 18, 0);
        widgetHolder.addSlot(catalyst, 36, 0);

        widgetHolder.addSlot(output, 94, 0).recipeContext(this);
    }
}
