package com.vulp.tomes.spells.active;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DarkAgeSpell extends ActiveSpell {

    public DarkAgeSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return 0;
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return false;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }

}
