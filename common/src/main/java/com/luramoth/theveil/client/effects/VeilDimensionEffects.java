package com.luramoth.theveil.client.effects;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class VeilDimensionEffects extends DimensionSpecialEffects {
    public VeilDimensionEffects() {
        super(Float.NaN, false, SkyType.NORMAL, false, true);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return new Vec3(0.16, 0.02,  0.38);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return true;
    }

    @Override
    public float @Nullable [] getSunriseColor(float timeOfDay, float partialTicks) {
        return null;
    }
}
