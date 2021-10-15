package com.vulp.tomes.spells.passive;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class DyingKnowledgeSpell extends PassiveSpell {

    public DyingKnowledgeSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    void fastTick(World world, Entity entity) {

    }

    @Override
    void slowTick(World world, Entity entity) {

    }

    @Override
    public boolean isTickable() {
        return false;
    }

}
