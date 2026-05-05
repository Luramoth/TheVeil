package com.luramoth.theveil.mixin;


import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.worldgen.TheVeilModDimensions;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    private void the_veil$fakeWaterInVeil(CallbackInfoReturnable<Boolean> cir){
        Entity entity = (Entity) (Object) this;
        if (entity.level() != null && entity.level().dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isEyeInFluid", at = @At("HEAD"), cancellable = true)
    private void the_veil$fakeLiquidInVeil(TagKey<Fluid> fluidTag, CallbackInfoReturnable<Boolean> cir){
        Entity entity = (Entity) (Object) this;

        if (entity.level() != null && entity.level().dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)){
            cir.setReturnValue(true);
        }
    }
}
