package com.vulp.tomes.spells.passive;

import com.vulp.tomes.init.EffectInit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

public class RottenHeartSpell extends PassiveSpell {

    public RottenHeartSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    void fastTick(World world, Entity entity) {

    }

    @Override
    void slowTick(World world, Entity entity) {
        if (!world.isRemote && entity instanceof LivingEntity) {
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(EffectInit.leaden_veins, 320, 0, true, false));
        }
    }

    @Override
    public boolean isTickable() {
        return true;
    }

}
