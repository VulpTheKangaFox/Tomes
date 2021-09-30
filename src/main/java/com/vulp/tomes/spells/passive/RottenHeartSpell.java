package com.vulp.tomes.spells.passive;

import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.util.SpellEnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class RottenHeartSpell extends PassiveSpell {

    public RottenHeartSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    void fastTick(World world, Entity entity) {

    }

    // TODO: Making animals run away is incredibly difficult apparently. Perhaps another downside instead? Or not maybe, considering these tomes are meant to be pretty hard to get in the first place.
    @Override
    void slowTick(World world, Entity entity) {
        if (!world.isRemote) {
        BlockPos pos = entity.getPosition();
        List<AnimalEntity> entityList = world.getEntitiesWithinAABB(AnimalEntity.class, new AxisAlignedBB(pos.add(64, 64, 64), pos.add(-64, -64, -64)));
        for (AnimalEntity animal : entityList) {
            PrioritizedGoal goal = new PrioritizedGoal(-1, new AvoidEntityGoal<>(animal, PlayerEntity.class, 6.0F, 0.9F, 1.5F));
            animal.goalSelector.goals.add(goal);
            }
        }
    }

    @Override
    public boolean isTickable() {
        return true;
    }

}
