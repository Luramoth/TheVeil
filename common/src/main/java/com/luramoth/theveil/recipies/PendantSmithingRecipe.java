package com.luramoth.theveil.recipies;

import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.data.CatalystManager;
import com.luramoth.theveil.items.TheVeilModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PendantSmithingRecipe implements SmithingRecipe {
    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.isEmpty();
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return stack.is(TheVeilModItems.PENDANT.get());
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return CatalystManager.getDimensionFor(stack.getItem()) != null;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return input.template().isEmpty() &&
                input.base().is(TheVeilModItems.PENDANT.get()) &&
                CatalystManager.getDimensionFor(input.addition().getItem()) != null &&
                !is_dim_already_unlocked(input);
    }

    private static boolean is_dim_already_unlocked(SmithingRecipeInput input){
        ItemStack pendant = input.base();
        ItemStack catalyst = input.addition();

        ResourceKey<Level> newDim = CatalystManager.getDimensionFor(catalyst.getItem());
        PendantData data = pendant.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);
        List<ResourceKey<Level>> dims = new ArrayList<>(data.unlockedDimensions());

        return dims.contains(newDim);
    }

    @Override
    public @NotNull ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack pendant = input.base().copy();
        ItemStack catalyst = input.addition();

        ResourceKey<Level> newDim = CatalystManager.getDimensionFor(catalyst.getItem());
        if (newDim != null) {
            PendantData data = pendant.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);
            List<ResourceKey<Level>> dims = new ArrayList<>(data.unlockedDimensions());

            if (!dims.contains(newDim)){
                dims.add(newDim);
                pendant.set(TheVeilModComponents.PENDENT_DATA.get(), new PendantData(dims, data.selectedIndex()));
            }
        }
        return pendant;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(TheVeilModItems.PENDANT.get());
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return TheVeilModRecipes.PENDANT_SMITHING.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    public static class Serialiser implements RecipeSerializer<PendantSmithingRecipe> {

        private static final PendantSmithingRecipe INSTANCE = new PendantSmithingRecipe();

        @Override
        public @NotNull MapCodec<PendantSmithingRecipe> codec() {
            return MapCodec.unit(INSTANCE);
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, PendantSmithingRecipe> streamCodec() {
            return StreamCodec.unit(INSTANCE);
        }
    }
}
