package com.vulp.tomes.spells.passive;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.ToggleableNearestAttackableTargetGoal;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class CovensRuleSpell extends PassiveSpell {

    public CovensRuleSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
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
