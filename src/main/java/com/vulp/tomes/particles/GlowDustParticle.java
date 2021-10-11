package com.vulp.tomes.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class GlowDustParticle extends SpriteTexturedParticle {

    private final int entityID;
    private double varX = 0;
    private double varY = 0;
    private double varZ = 0;

    private GlowDustParticle(ClientWorld world, double x, double y, double z, int entityID, double velY, double velZ) {
        super(world, x, y, z, 0.0F, 0.0F, 0.0F);
        Random rand = new Random();
        this.setSize(0.02F, 0.02F);
        this.entityID = entityID;
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.5F;
        Entity entity = this.world.getEntityByID(this.entityID);
        if (entity != null) {
            float width = entity.getWidth();
            float height = entity.getHeight();
            this.varX = (rand.nextFloat() - 0.5F) * 2.0F * width;
            this.varY = (rand.nextFloat() - 0.5F) * 2.0F * height + (height / 2.0F);
            this.varZ = (rand.nextFloat() - 0.5F) * 2.0F * width;
        }
        this.motionX *= (double)0.02F;
        this.motionY *= (double)0.02F;
        this.motionZ *= (double)0.02F;
        this.maxAge = (int)(30.0D / (Math.random() * 0.8D + 0.6D));
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.maxAge-- <= 0) {
            this.setExpired();
        } else {
            Entity entity = this.world.getEntityByID(this.entityID);
            if (entity != null) {
                this.posX = entity.getPosX() + this.varX;
                this.posY = entity.getPosY() + this.varY;
                this.posZ = entity.getPosZ() + this.varZ;
                this.varX *= 0.98D + (rand.nextDouble() * 0.04);
                this.varY *= 0.98D + (rand.nextDouble() * 0.04);
                this.varZ *= 0.98D + (rand.nextDouble() * 0.04);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class WildWolfFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public WildWolfFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double entityID, double ySpeed, double zSpeed) {
            GlowDustParticle particle = new GlowDustParticle(worldIn, x, y, z, (int) entityID, ySpeed, zSpeed);
            particle.selectSpriteRandomly(this.spriteSet);
            particle.setColor(0.25F, 0.88F, 0.0F);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SpectralSteedFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public SpectralSteedFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double entityID, double ySpeed, double zSpeed) {
            GlowDustParticle particle = new GlowDustParticle(worldIn, x, y, z, (int) entityID, ySpeed, zSpeed);
            particle.selectSpriteRandomly(this.spriteSet);
            particle.setColor(0.7F, 1.0F, 0.90F);
            return particle;
        }
    }

}
