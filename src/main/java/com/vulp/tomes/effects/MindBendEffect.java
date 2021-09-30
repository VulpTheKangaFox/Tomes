package com.vulp.tomes.effects;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.entities.ai.MindBendFollowGoal;
import com.vulp.tomes.entities.ai.NullifyAttackableTargetGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectType;

public class MindBendEffect extends TomeEffect {

    public MindBendEffect() {
        super(EffectType.HARMFUL, 9830455, true);
    }

    @Override
    public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        CompoundNBT nbt = entityLivingBaseIn.getPersistentData();
        if (!nbt.hasUniqueId("PlayerFollowingUUID")) {
            Tomes.LOGGER.error("MindBendEffect applied through unusual means or commands. Effect will likely not cause anything to happen.");
        }
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    @Override
    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        entityLivingBaseIn.getPersistentData().remove("PlayerFollowingUUID");
        if (entityLivingBaseIn instanceof MobEntity) {
            ((MobEntity) entityLivingBaseIn).goalSelector.removeGoal(new MindBendFollowGoal((MobEntity) entityLivingBaseIn, 1.0F, 5.0F, 2.0F));
            ((MobEntity) entityLivingBaseIn).targetSelector.removeGoal(new NullifyAttackableTargetGoal((MobEntity) entityLivingBaseIn, false));
        }
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    @Override
    void potionTick(LivingEntity entityLivingBaseIn, int amplifier) {
    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return false;
    }

}
