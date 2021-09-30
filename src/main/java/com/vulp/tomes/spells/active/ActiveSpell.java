package com.vulp.tomes.spells.active;

import com.vulp.tomes.spells.Spell;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class ActiveSpell extends Spell {

    private Entity target;

    public ActiveSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
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
