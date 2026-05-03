package com.luramoth.theveil.items;

import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import io.wispforest.accessories.api.Accessory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class PendentItem extends Item implements Accessory {
    public PendentItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        PendantData data = stack.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);

        tooltipComponents.add(Component.translatable("item.the_veil.pendant.description1").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(Component.translatable("item.the_veil.pendant.description2").withStyle(ChatFormatting.YELLOW));

        if (!data.unlockedDimensions().isEmpty()) {
            tooltipComponents.add(Component.translatable("item.the_veil.pendant.unlocked_dimensions"));
        } else {
            tooltipComponents.add(Component.translatable("item.the_veil.pendant.inert").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }
}
