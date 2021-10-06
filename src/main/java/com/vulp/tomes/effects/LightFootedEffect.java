package com.vulp.tomes.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class LightFootedEffect extends TomeEffect {

    private int timer = 5;

    public LightFootedEffect() {
        super(EffectType.BENEFICIAL, 8191918, true);
    }

    @Override
    void potionTick(LivingEntity entityLivingBaseIn, int amplifier) {
        World world = entityLivingBaseIn.world;
        if (entityLivingBaseIn.isOnGround()) {
            if (this.timer <= 0) {
                this.timer = 5;
                entityLivingBaseIn.removePotionEffect(this);
            } else if (this.timer == 5) {
                if (world.isRemote) {
                    spawnParticles(entityLivingBaseIn);
                }
            }
            this.timer--;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnParticles(LivingEntity entity) {
        Random rand = new Random();
        if (entity != null) {
            for (int i = 0; i < 10; i++) {
                entity.getEntityWorld().addParticle(ParticleTypes.POOF, entity.getPosX(), entity.getPosY() + 0.05, entity.getPosZ(), (rand.nextFloat() - rand.nextFloat()) * 0.15, (rand.nextFloat() - rand.nextFloat()) * 0.05, (rand.nextFloat() - rand.nextFloat()) * 0.15);
            }
        }
    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return true;
    }
}
