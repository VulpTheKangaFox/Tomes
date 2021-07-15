package com.vulp.tomes.spells.passive;

import com.vulp.tomes.spells.Spell;
import net.minecraft.enchantment.Enchantment;

public abstract class PassiveSpell extends Spell {

    private int ticker = 0;

    public PassiveSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    public void tick() {
        this.fastTick();
        if (this.ticker >= 20) {
            this.slowTick();
            this.ticker = 0;
        } else {
            this.ticker++;
        }
    }

    abstract void fastTick();

    abstract void slowTick();

}
