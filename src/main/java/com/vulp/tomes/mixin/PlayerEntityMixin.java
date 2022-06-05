package com.vulp.tomes.mixin;

import com.vulp.tomes.init.EffectInit;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;noClip:Z", opcode = Opcodes.PUTFIELD), method = "tick")
    public void tickedNoClip(PlayerEntity playerEntity, boolean value) {
        getThis().noClip = playerEntity.isPotionActive(EffectInit.starry_form) || value;
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private PlayerEntity getThis() {
        return ((PlayerEntity)(Object)this);
    }

}
