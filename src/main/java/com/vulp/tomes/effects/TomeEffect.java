package com.vulp.tomes.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

public abstract class TomeEffect extends Effect {

    private final boolean customTickEvent;

    public TomeEffect(EffectType typeIn, int liquidColorIn, boolean customTickEvent) {
        super(typeIn, liquidColorIn);
        this.customTickEvent = customTickEvent;
    }

    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        if (this.customTickEvent) {
            potionTick(entityLivingBaseIn, amplifier);
        } else {
            super.performEffect(entityLivingBaseIn, amplifier);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        if (this.customTickEvent) {
            return readyToTick(duration, amplifier);
        } else {
            return super.isReady(duration, amplifier);
        }
    }

    abstract void potionTick(LivingEntity entityLivingBaseIn, int amplifier);

    abstract boolean readyToTick(int duration, int amplifier);

}
