package com.vulp.tomes.spells.active;

import com.vulp.tomes.spells.Spell;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;

public abstract class ActiveSpell extends Spell {

    private Entity target;

    public ActiveSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        super(rarity, isActive, isRare, enabled);
    }

    public abstract int getSpellCost();

    public abstract boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn);

    public void setTarget(@Nullable Entity entity) {
        this.target = entity;
    }

    @Nullable
    public Entity getTarget() {
        return this.target;
    }

    public abstract int getCooldown();

    public abstract boolean canTick();

    public abstract void tick(World world, Entity entity);

    @Override
    public void tickEvent(World world, Entity entity) {
        if (canTick()) {
            this.tick(world, entity);
        }
    }

}
