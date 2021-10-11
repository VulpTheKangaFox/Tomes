package com.vulp.tomes.mixin;

import com.vulp.tomes.init.EffectInit;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.util.SpellEnchantUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodStats.class)
abstract class FoodStatsMixin {

    private boolean healToggle = false;

    @Shadow private float foodSaturationLevel;
    @Shadow private int foodLevel;
    @Shadow private int foodTimer;

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"), method = "tick")
    private void tick(PlayerEntity player, float healAmount) {
        if (!player.world.isRemote) {
            boolean flag = player.isPotionActive(EffectInit.leaden_veins);
            if (this.foodSaturationLevel > 0.0F && player.shouldHeal() && this.foodLevel >= 20) {
                float f = Math.min(this.foodSaturationLevel, 6.0F);
                if (flag) {
                    player.heal((f / 6.0F) / 2.0F);
                } else {
                    player.heal(f / 6.0F);
                }
                this.addExhaustion(f);
                this.foodTimer = 0;
            } else if (this.foodLevel >= 18 && player.shouldHeal()) {
                if (this.healToggle || !flag) {
                    player.heal(1.0F);
                }
                this.healToggle = !this.healToggle;
                this.addExhaustion(6.0F);
                this.foodTimer = 0;
            }
        }
    }// diamond, heart, jack, spade

}
