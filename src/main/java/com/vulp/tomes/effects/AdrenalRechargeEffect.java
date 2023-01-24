package com.vulp.tomes.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;

public class AdrenalRechargeEffect extends TomeEffect {

    public AdrenalRechargeEffect() {
        super(EffectType.HARMFUL, 0, false);
    }

    @Override
    void potionTick(LivingEntity entityLivingBaseIn, int amplifier) {

    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return false;
    }

}