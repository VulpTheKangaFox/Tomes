package com.vulp.tomes.spells.active;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.init.EffectInit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class SelfPropulsionSpell extends ActiveSpell {

    private int betweenTimer;
    private int totalTimer;

    public SelfPropulsionSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return 8;
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.addVelocity(playerIn.getLookVec().x * 1.4, playerIn.getLookVec().y * 1.4, playerIn.getLookVec().z * 1.4);
        this.betweenTimer = 2;
        this.totalTimer = 30;
        if (worldIn.isRemote) {
            Random rand = new Random();
            for (int i = 0; i < 10; i++) {
                worldIn.addParticle(ParticleTypes.POOF, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), (rand.nextFloat() - rand.nextFloat()) * 0.1, (rand.nextFloat() - rand.nextFloat()) * 0.1, (rand.nextFloat() - rand.nextFloat()) * 0.1);
            }
        } else {
            playerIn.addPotionEffect(new EffectInstance(EffectInit.light_footed, 65, 0, false, false));
        }
        return true;
    }

    @Override
    public int getCooldown() {
        return 50;
    }

    @Override
    public boolean canTick() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tick(World world, Entity entity) {
        if (this.totalTimer > 0) {
            if (betweenTimer <= 0) {
                Random rand = new Random();
                world.addParticle(ParticleTypes.POOF, entity.getPosX(), entity.getPosY(), entity.getPosZ(), (rand.nextFloat() - rand.nextFloat()) * 0.1, (rand.nextFloat() - rand.nextFloat()) * 0.1, (rand.nextFloat() - rand.nextFloat()) * 0.1);
                this.betweenTimer = 2;
            } else this.betweenTimer--;
            this.totalTimer--;
        }
    }

}
