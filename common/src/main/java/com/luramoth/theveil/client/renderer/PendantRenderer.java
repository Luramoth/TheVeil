package com.luramoth.theveil.client.renderer;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.items.PendentItem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PendantRenderer implements AccessoryRenderer {
    private static final ResourceLocation BASE_TEX = ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, "textures/models/armor/pendant_layer_base.png");
    private static final ResourceLocation GEM_TEX = ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, "textures/models/armor/pendant_layer_gem.png");

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        model.renderToBuffer(matrices,
                multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(BASE_TEX)),
                light, OverlayTexture.NO_OVERLAY, -1);

        int color = PendentItem.getGemColor(stack);
        model.renderToBuffer(matrices,
                multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(GEM_TEX)),
                light, OverlayTexture.NO_OVERLAY, color);
    }
}
