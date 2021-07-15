package com.vulp.tomes.spells;

import com.vulp.tomes.enchantments.EnchantmentTypes;
import net.minecraft.enchantment.Enchantment;

public class Spell {

    private final Enchantment.Rarity rarity;
    private final boolean isRare;
    private final boolean isActive;

    public Spell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        this.rarity = rarity;
        this.isRare = isRare;
        this.isActive = isActive;
    }

    public Enchantment.Rarity getRarity() {
        return this.rarity;
    }

    public boolean isRare() {
        return this.isRare;
    }

    public boolean isActive() {
        return this.isActive;
    }

}
