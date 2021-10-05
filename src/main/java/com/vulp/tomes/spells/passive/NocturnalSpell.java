package com.vulp.tomes.spells.passive;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class NocturnalSpell extends PassiveSpell {

    public NocturnalSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    void fastTick(World world, Entity entity) {

    }

    @Override
    void slowTick(World world, Entity entity) {
        if (!world.isRemote) {
            if (entity instanceof LivingEntity && world.isNightTime()) {
                LivingEntity user = (LivingEntity) entity;
                user.addPotionEffect(new EffectInstance(Effects.STRENGTH, 120, 0, true, false));
                user.addPotionEffect(new EffectInstance(Effects.SPEED, 120, 0, true, false));
                user.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 280, 0, true, false));
            }
        }
    }

    @Override
    public boolean isTickable() {
        return true;
    }
}
