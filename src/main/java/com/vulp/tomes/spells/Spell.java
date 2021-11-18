package com.vulp.tomes.spells;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class Spell {

    private final Enchantment.Rarity rarity;
    private final boolean isRare;
    private final boolean isActive;
    private final ForgeConfigSpec.ConfigValue<Boolean> enabled;

    public Spell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        this.rarity = rarity;
        this.isRare = isRare;
        this.isActive = isActive;
        this.enabled = enabled;
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

    public boolean isDisabled() {
        return !this.enabled.get();
    }

    public abstract void tickEvent(World world, Entity entity);

}
