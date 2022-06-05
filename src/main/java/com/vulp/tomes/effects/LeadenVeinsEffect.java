package com.vulp.tomes.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;

public class LeadenVeinsEffect extends TomeEffect {

    public LeadenVeinsEffect() {
        super(EffectType.HARMFUL, 3812932, false);
    }

    @Override
    void potionTick(LivingEntity entityLivingBaseIn, int amplifier) {

    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return false;
    }

}
