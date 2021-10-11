package com.vulp.tomes.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;

public class LightFootedEffect extends TomeEffect {

    private int timer = 5;

    public LightFootedEffect() {
        super(EffectType.BENEFICIAL, 8191918, true);
    }

    @Override
    void potionTick(LivingEntity entity, int amplifier) {
        if (entity.isOnGround()) {
            if (this.timer <= 0) {
                this.timer = 5;
                entity.removePotionEffect(this);
            }
            this.timer--;
        }
    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return true;
    }
}
