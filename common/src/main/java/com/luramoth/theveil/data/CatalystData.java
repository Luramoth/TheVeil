package com.luramoth.theveil.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record CatalystData(ResourceKey<Level> dim, int color) {
}
