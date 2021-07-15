package com.vulp.tomes.spells.active;

import com.vulp.tomes.Tomes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SelfPropulsionSpell extends ActiveSpell {

    public SelfPropulsionSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return 8;
    }

    @Override
    public void onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        Tomes.LOGGER.debug(playerIn.getLookVec().x + " | " + playerIn.getLookVec().y + " | " + playerIn.getLookVec().z);
        playerIn.addVelocity(playerIn.getLookVec().x * 1.4, playerIn.getLookVec().y * 1.4, playerIn.getLookVec().z * 1.4);
        Tomes.LOGGER.debug("CASTED!");
    }

    @Override
    public int getCooldown() {
        return 50;
    }

}
