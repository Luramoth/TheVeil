package com.luramoth.theveil.mixin.client;

import com.luramoth.theveil.worldgen.TheVeilModDimensions;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void the_veil$setupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean shouldCreateFog, float partialTick, CallbackInfo ci) {
        if (camera.getEntity().level().dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)){
            RenderSystem.setShaderFogStart(0.0f);
            RenderSystem.setShaderFogEnd(16.0f);
        }
    }

    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void the_veil$setupColor(Camera activeRenderInfo, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        if (level.dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)){
            float pitch = activeRenderInfo.getXRot();

            float factor = (pitch + 90.0f) / 180.0f;
            float brightness = 1.2f - (factor * 0.8f);

            RenderSystem.clearColor(
                    0.0210f * brightness,
                    0.00f * brightness,
                    0.0700f * brightness,
                    1.0f);
        }
    }
}
