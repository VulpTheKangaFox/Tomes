package com.vulp.tomes.effects;

import com.vulp.tomes.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;

public class WingedEffect extends TomeEffect {

    public WingedEffect() {
        super(EffectType.BENEFICIAL, 8534783, false);
    }

    @Override
    void potionTick(LivingEntity entityLivingBaseIn, int amplifier) {

    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return false;
    }

    @Override
    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
        entityLivingBaseIn.addPotionEffect(new EffectInstance(EffectInit.light_footed, 200, 0, true, false));
    }
}