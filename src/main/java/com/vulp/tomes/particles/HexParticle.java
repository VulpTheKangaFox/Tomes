package com.vulp.tomes.particles;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class HexParticle extends YawLockedParticle {

    private final IAnimatedSprite spriteWithAge;
    private final int entityID;
    private final double varX;
    private final double varY;
    private final double varZ;

    // Once you have the particle permanence idea working, switch over to that method?
    protected HexParticle(ClientWorld world, double x, double y, double z, int entityID, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z);
        Random rand = new Random();
        this.varX = rand.nextFloat() * 0.02F;
        this.varY = rand.nextFloat() * 0.02F;
        this.varZ = rand.nextFloat() * 0.02F;
        this.particleScale *= 1.5F;
        LivingEntity entity = (LivingEntity) this.world.getEntityByID(entityID);
        if (entity != null) {
            this.posX = entity.getPosX() + varX;
            this.posY = entity.getPosY() + varY + entity.getHeight() + 0.5F;
            this.posZ = entity.getPosZ() + varZ;
        }
        this.maxAge = 25;
        this.spriteWithAge = spriteWithAge;
        this.entityID = entityID;
        this.selectSpriteWithAge(spriteWithAge);
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
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
/*            if (this.age < 5) {
                this.particleAlpha = 0.0F;
            } else {
                this.particleAlpha = 1.0F;
            }*/
        }
        LivingEntity entity = (LivingEntity) this.world.getEntityByID(this.entityID);
        if (entity != null) {
            this.posX = entity.getPosX() + varX;
            this.posY = entity.getPosY() + varY + entity.getHeight() + 0.5F;
            this.posZ = entity.getPosZ() + varZ;
        }
            this.selectSpriteWithAge(this.spriteWithAge);
    }

    @Override
    public float getScale(float scaleFactor) {
        float f = ((float)this.age + scaleFactor + 5) / (float)this.maxAge;
        return this.particleScale * (1.0F - f * f * 0.5F);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HexParticle(worldIn, x, y, z, (int)xSpeed, this.spriteSet);
        }
    }

}
