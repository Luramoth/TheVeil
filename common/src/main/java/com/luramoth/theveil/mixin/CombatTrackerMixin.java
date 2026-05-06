package com.luramoth.theveil.mixin;

import com.luramoth.theveil.worldgen.TheVeilModDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CombatTracker.class)
public class CombatTrackerMixin {
    @Shadow
    @Final
    private LivingEntity mob;

    @Inject(method = "getDeathMessage", at = @At("RETURN"), cancellable = true)
    private void the_veil$overrideVoidDeathMessage(CallbackInfoReturnable<Component> cir){
        LivingEntity entity = (LivingEntity) (Object) mob;

        if (entity.level().dimension().equals(TheVeilModDimensions.THE_VEIL_KEY)) {
            DamageSource lastSource = mob.getLastDamageSource();

            if (lastSource != null && lastSource.is(DamageTypes.FELL_OUT_OF_WORLD)) {
                cir.setReturnValue(Component.translatable("death.attack.the_veil.pressure", mob.getDisplayName()));
            }
        }
    }
}
