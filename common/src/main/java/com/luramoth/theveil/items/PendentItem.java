package com.luramoth.theveil.items;

import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.data.CatalystManager;
import io.wispforest.accessories.api.Accessory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class PendentItem extends Item implements Accessory {
    public PendentItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        PendantData data = stack.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);

        tooltipComponents.add(Component.translatable("item.the_veil.pendant.description1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("item.the_veil.pendant.description2").withStyle(ChatFormatting.GOLD));

        if (!data.unlockedDimensions().isEmpty()) {
            tooltipComponents.add(Component.translatable("item.the_veil.pendant.unlocked_dimensions"));

            for (ResourceKey<Level> dim : data.unlockedDimensions()){
                String translationKey = "dimension." + dim.location().getNamespace() + "." + dim.location().getPath();

                if (dim == data.unlockedDimensions().get(data.selectedIndex())) {
                    tooltipComponents.add(Component.literal(" -> ")
                            .append(Component.translatable(translationKey)));
                } else {
                    tooltipComponents.add(Component.literal(" - ")
                            .append(Component.translatable(translationKey))
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        } else {
            tooltipComponents.add(Component.translatable("item.the_veil.pendant.inert").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }

    public static int getGemColor(ItemStack stack) {
        PendantData data = stack.get(TheVeilModComponents.PENDENT_DATA.get());
        if (data == null || data.unlockedDimensions().isEmpty()) return 0xffffff;

        ResourceKey<Level> selected = data.unlockedDimensions().get(data.selectedIndex());

        return CatalystManager.getColorFor(selected);
    }
}
