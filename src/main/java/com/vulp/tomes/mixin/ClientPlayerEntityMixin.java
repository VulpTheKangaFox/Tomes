package com.vulp.tomes.mixin;

import com.vulp.tomes.init.EffectInit;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CEntityActionPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow public abstract boolean isForcedDown();

    @Shadow @Final public ClientPlayNetHandler connection;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isForcedDown()Z"), method = "livingTick")
    public boolean livingTickIsForcedDown(ClientPlayerEntity clientPlayerEntity) {
        return !clientPlayerEntity.isPotionActive(EffectInit.starry_form) && isForcedDown();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;getItemStackFromSlot(Lnet/minecraft/inventory/EquipmentSlotType;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), method = "livingTick", cancellable = true)
    public void livingTickGetItemStackFromSlot(CallbackInfo ci)
    {
        if (getThis().isPotionActive(EffectInit.winged) && getThis().tryToStartFallFlying()) {
            this.connection.sendPacket(new CEntityActionPacket(getThis(), CEntityActionPacket.Action.START_FALL_FLYING));
        }
    }

    // Allows us to reference the current instance of the class the mixin is injecting.
    private ClientPlayerEntity getThis() {
        return ((ClientPlayerEntity)(Object)this);
    }

}
