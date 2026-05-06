package com.luramoth.theveil.mixin.client;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.worldgen.TheVeilModDimensions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class VeilSkyMixin {

    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void the_veil$renderVeilSky(Matrix4f  frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        if (this.level != null && this.level.dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)) {
            the_veil$renderGradientSky(frustumMatrix, camera.getPosition().y);
            ci.cancel();
        }
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void the_veil$stopClouds(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (this.level != null && this.level.dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)) {
            ci.cancel();
        }
    }

    // makes a giant cylinder around the player and let vertex colors make the gradient
    @Unique
    private void the_veil$renderGradientSky(Matrix4f matrix, double camY) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float size = 150.0f;
        int segments = 16;
        float altitudeFactor = (float) ((camY + 20) / 200);
        altitudeFactor = Mth.clamp(altitudeFactor, 0.0f, 1.5f);

        // top vertex colors
        float tr = 0.16f * altitudeFactor, tg = 0.02f * altitudeFactor, tb = 0.38f * altitudeFactor, ta = 1.0f;
        // bottom vertex colors
        float br = 0.02f * altitudeFactor, bg = 0.00f * altitudeFactor, bb = 0.06f * altitudeFactor, ba = 1.0f;

        for (int i = 0; i < segments; i++) {
            double angle1 = (i * 2 * Math.PI) / segments;
            double angle2 = ((i + 1) * 2 * Math.PI) /segments;

            float x1 = (float) (Math.cos(angle1) * size);
            float z1 = (float) (Math.sin(angle1) * size);
            float x2 = (float) (Math.cos(angle2) * size);
            float z2 = (float) (Math.sin(angle2) * size);

            // wall quad
            bufferBuilder.addVertex(matrix, x1, -size, z1).setColor(br, bg, bb, ba);
            bufferBuilder.addVertex(matrix, x2, -size, z2).setColor(br, bg, bb, ba);
            bufferBuilder.addVertex(matrix, x2, size, z2).setColor(tr, tg, tb, ta);
            bufferBuilder.addVertex(matrix, x1, size, z1).setColor(tr, tg, tb, ta);

            // top cap
            bufferBuilder.addVertex(matrix, 0,  size, 0).setColor(tr, tg, tb, ta);   // center
            bufferBuilder.addVertex(matrix, 0,  size, 0).setColor(tr, tg, tb, ta);   // repeat center for Quad
            bufferBuilder.addVertex(matrix, x2, size, z2).setColor(tr, tg, tb, ta);  // edge 2
            bufferBuilder.addVertex(matrix, x1, size, z1).setColor(tr, tg, tb, ta);  // edge 1

            // bottom cap
            bufferBuilder.addVertex(matrix, 0,  -size, 0).setColor(br, bg, bb, ba);
            bufferBuilder.addVertex(matrix, 0,  -size, 0).setColor(br, bg, bb, ba);
            bufferBuilder.addVertex(matrix, x1, -size, z1).setColor(br, bg, bb, ba);
            bufferBuilder.addVertex(matrix, x2, -size, z2).setColor(br, bg, bb, ba);
        }


        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.enableCull();
        RenderSystem.disableCull();
        RenderSystem.depthMask(true);
    }
}
