package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.entities.projectile.WitheringStenchEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WitheringStenchSpell extends ActiveSpell {

    public WitheringStenchSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.withering_stench_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            for (int i = 0; i < 7; i++) {
                WitheringStenchEntity proj = new WitheringStenchEntity(worldIn, playerIn);
                double d0 = playerIn.getLookVec().getX();
                double d1 = playerIn.getLookVec().getY();
                double d2 = playerIn.getLookVec().getZ();
                proj.shoot(d0, d1, d2, 1.0F, 20.0F);
                worldIn.addEntity(proj);
            }
        }
        return true;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.withering_stench_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }
}
