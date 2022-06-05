package com.vulp.tomes.mixin;

import com.vulp.tomes.events.EntityEvents;
import com.vulp.tomes.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Effect.class)
public class EffectMixin {

    // Triggers on poison damage.
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 0), method = "performEffect", cancellable = true)
    public void performEffectPoison(LivingEntity entityLivingBaseIn, int amplifier, CallbackInfo ci) {
        if (entityLivingBaseIn.isPotionActive(EffectInit.antidotal)) {
            EffectInstance instance = entityLivingBaseIn.getActivePotionEffect(EffectInit.antidotal);
            if (instance != null && !entityLivingBaseIn.getEntityWorld().isRemote()) {
                int toggle = 0;
                if (!EntityEvents.poison_resistance_toggle.isEmpty() && EntityEvents.poison_resistance_toggle.containsKey(entityLivingBaseIn)) {
                    toggle = EntityEvents.poison_resistance_toggle.get(entityLivingBaseIn) + 1;
                    if (toggle > instance.getAmplifier() + 1) {
                        toggle = 0;
                    }
                }
                EntityEvents.poison_resistance_toggle.remove(entityLivingBaseIn);
                EntityEvents.poison_resistance_toggle.put(entityLivingBaseIn, toggle);
                if (toggle != 0) {
                    ci.cancel();
                }
            }
        }
    }

    // Triggers on hunger.
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), method = "performEffect", cancellable = true)
    public void performEffectHunger(LivingEntity entityLivingBaseIn, int amplifier, CallbackInfo ci) {
        if (entityLivingBaseIn.isPotionActive(EffectInit.antidotal)) {
            EffectInstance instance = entityLivingBaseIn.getActivePotionEffect(EffectInit.antidotal);
            if (instance != null && !entityLivingBaseIn.getEntityWorld().isRemote()) {
                int toggle = 0;
                if (!EntityEvents.hunger_resistance_toggle.isEmpty() && EntityEvents.hunger_resistance_toggle.containsKey(entityLivingBaseIn)) {
                    toggle = EntityEvents.hunger_resistance_toggle.get(entityLivingBaseIn) + 1;
                    if (toggle > instance.getAmplifier() + 1) {
                        toggle = 0;
                    }
                }
                EntityEvents.hunger_resistance_toggle.remove(entityLivingBaseIn);
                EntityEvents.hunger_resistance_toggle.put(entityLivingBaseIn, toggle);
                if (toggle != 0) {
                    ci.cancel();
                }
            }
        }
    }

}
