package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ForceOfWindSpell extends ActiveSpell {

    public ForceOfWindSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.force_of_wind_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        double lookX = playerIn.getLookVec().x;
        double lookY = playerIn.getLookVec().y;
        double lookZ = playerIn.getLookVec().z;
        Vector3d playerPos = playerIn.getPositionVec();
        Vector3d lookingPos = playerPos.add(lookX * 3, lookY * 3, lookZ * 3);
        if (!worldIn.isRemote) {
            List<LivingEntity> list = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(lookingPos.add(-4, -4, -4), lookingPos.add(4, 4, 4)), (livingEntity -> livingEntity != playerIn));
            list.forEach(livingEntity -> livingEntity.addVelocity(lookX * 1.75, lookY * 1.75, lookZ * 1.75));
        } else {
            Random rand = new Random();
            for (int i = 0; i < 30; i++) {
                worldIn.addParticle(ParticleTypes.POOF, (float) lookingPos.getX() + (rand.nextFloat() * 8.0F) - 4.0F, (float) lookingPos.getY() + (rand.nextFloat() * 8.0F) - 4.0F, (float) lookingPos.getZ() + (rand.nextFloat() * 8.0F) - 4.0F, lookX, lookY, lookZ);
            }
        }
        return true;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.force_of_wind_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }

}
