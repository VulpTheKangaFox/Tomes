package com.vulp.tomes.spells.active;

import com.vulp.tomes.spells.Spell;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class ActiveSpell extends Spell {

    public ActiveSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    public abstract int getSpellCost();

    public abstract void onCast(World worldIn, PlayerEntity playerIn, Hand handIn);

    public abstract int getCooldown();

}
