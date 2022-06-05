package com.vulp.tomes.mixin;

import com.vulp.tomes.init.EffectInit;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow public abstract boolean isForcedDown();

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isForcedDown()Z"), method = "livingTick")
    public boolean livingTick(ClientPlayerEntity clientPlayerEntity) {
        return !clientPlayerEntity.isPotionActive(EffectInit.starry_form) && isForcedDown();
    }

}
