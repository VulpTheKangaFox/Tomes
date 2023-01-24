package com.vulp.tomes.mixin;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.init.EffectInit;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract void startFallFlying();

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;noClip:Z", opcode = Opcodes.PUTFIELD), method = "tick")
    public void tickedNoClip(PlayerEntity playerEntity, boolean value) {
        getThis().noClip = playerEntity.isPotionActive(EffectInit.starry_form) || value;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getItemStackFromSlot(Lnet/minecraft/inventory/EquipmentSlotType;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE), method = "tryToStartFallFlying", cancellable = true)
    public void tryToStartElytraFallingMetamorphosis(CallbackInfoReturnable<Boolean> cir) {
        this.startFallFlying();
        cir.setReturnValue(true);
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private PlayerEntity getThis() {
        return ((PlayerEntity)(Object)this);
    }

}
