package com.vulp.tomes.spells.active;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ForceOfWindSpell extends ActiveSpell {

    public ForceOfWindSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return 10;
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        double lookX = playerIn.getLookVec().x;
        double lookY = playerIn.getLookVec().y;
        double lookZ = playerIn.getLookVec().z;
        BlockPos playerPos = playerIn.getPosition();
        BlockPos lookingPos = playerPos.add(lookX * 3, lookY * 3, lookZ * 3);
        List<LivingEntity> list = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(lookingPos.add(-3, -3, -3), lookingPos.add(3, 3, 3)), (livingEntity -> livingEntity != playerIn));
        list.forEach(livingEntity -> livingEntity.addVelocity(lookX * 1.5, lookY * 1.5, lookZ * 1.5));
        return true;
    }

    @Override
    public int getCooldown() {
        return 70;
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }

}
