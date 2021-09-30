package com.vulp.tomes.spells.passive;

import com.vulp.tomes.spells.Spell;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class PassiveSpell extends Spell {

    private int ticker = 0;

    public PassiveSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    public void tickEvent(World world, Entity entity) {
        if (this.isTickable()) {
            if (this.ticker >= 20) {
                this.slowTick(world, entity);
                this.ticker = 0;
            } else {
                this.ticker++;
            }
            this.fastTick(world, entity);
        }
    }

    abstract void fastTick(World world, Entity entity);

    abstract void slowTick(World world, Entity entity);

    public abstract boolean isTickable();

}
