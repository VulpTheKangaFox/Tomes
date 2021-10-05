package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DarkAgeSpell extends ActiveSpell {

    public DarkAgeSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.dark_age_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote && !worldIn.isNightTime()) {
            ((ServerWorld)worldIn).setDayTime(13000L);
            return true;
        }
        return false;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.dark_age_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }

}
