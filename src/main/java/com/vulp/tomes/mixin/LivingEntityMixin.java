package com.vulp.tomes.mixin;

import com.vulp.tomes.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "updateElytra", at = @At(value = "HEAD"), cancellable = true)
    public void updateElytraHijacker(CallbackInfo ci) {
        if (getThis().getFlag(7) && getThis().isPotionActive(EffectInit.winged)) {
            if (!getThis().world.isRemote && !getThis().isOnGround() && !getThis().isPassenger() && !getThis().isPotionActive(Effects.LEVITATION)) {
                getThis().setFlag(7, true);
                ci.cancel();
            }
        }
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private LivingEntity getThis() {
        return ((LivingEntity)(Object)this);
    }

}
