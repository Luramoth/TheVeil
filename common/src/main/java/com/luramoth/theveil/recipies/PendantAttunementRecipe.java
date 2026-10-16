package com.luramoth.theveil.recipies;

import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.items.TheVeilModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record PendantAttunementRecipe(Item catalyst, ResourceKey<Level> dimension, int color) implements SmithingRecipe {
    private static final Codec<Integer> HEX_COLOR = Codec.STRING.comapFlatMap(
            s -> {
                try {
                    return DataResult.success(Integer.parseInt(s, 16) | 0xFF000000);
                } catch (NumberFormatException e) {
                    return DataResult.error(() -> "Invalid hex color '" + s +"', expected RRGGBB");
                }
            },
            c -> String.format("%06X", c & 0xFFFFFF));

    public static final MapCodec<PendantAttunementRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("catalyst").forGetter(PendantAttunementRecipe::catalyst),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(PendantAttunementRecipe::dimension),
            HEX_COLOR.fieldOf("color").forGetter(PendantAttunementRecipe::color)
    ).apply(i, PendantAttunementRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PendantAttunementRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM), PendantAttunementRecipe::catalyst,
            ResourceKey.streamCodec(Registries.DIMENSION), PendantAttunementRecipe::dimension,
            ByteBufCodecs.INT, PendantAttunementRecipe::color,
            PendantAttunementRecipe::new
    );

    //--------smithing

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
        return stack.is(catalyst);
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return input.template().isEmpty()
                && input.base().is(TheVeilModItems.PENDANT.get())
                && input.addition().is(catalyst)
                && !dataOf(input.base()).unlockedDimensions().contains(dimension);
    }

    @Override
    public @NotNull ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack pendant = input.base().copyWithCount(1);
        PendantData data = dataOf(pendant);

        if (!data.unlockedDimensions().contains(dimension)) {
            List<ResourceKey<Level>> dims = new ArrayList<>(data.unlockedDimensions());
            dims.add(dimension);
            pendant.set(TheVeilModComponents.PENDENT_DATA.get(), data.withDimensions(dims));
        }
        return pendant;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack pendant = new ItemStack(TheVeilModItems.PENDANT.get());
        pendant.set(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT.withDimensions(List.of(dimension)));
        return pendant;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }
    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return TheVeilModRecipes.PENDANT_ATTUNEMENT.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    private static PendantData dataOf(ItemStack pendant) {
        return pendant.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);
    }

    public static class Serializer implements RecipeSerializer<PendantAttunementRecipe> {
        @Override
        public @NotNull MapCodec<PendantAttunementRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, PendantAttunementRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
