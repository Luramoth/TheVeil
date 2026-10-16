package com.luramoth.theveil.client;

import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.data.CatalystManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PendantGemColor {
    private PendantGemColor() {}

    public static int get(ItemStack stack) {
        PendantData data = stack.get(TheVeilModComponents.PENDENT_DATA.get());
        if (data == null || data.unlockedDimensions().isEmpty()) return 0xffffff;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return CatalystManager.FALLBACK_COLOR;

        ResourceKey<Level> selected = data.unlockedDimensions().get(data.selectedIndex());
        return CatalystManager.getColorFor(level.getRecipeManager(), selected);
    }
}
