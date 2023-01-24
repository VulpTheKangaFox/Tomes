package com.vulp.tomes.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class GobletParticle extends SpriteTexturedParticle {

    private final float floatSpeed;

    protected GobletParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.particleAlpha = 1.0F;
        this.setColor(0.0F, 1.0F, 1.0F);
        this.floatSpeed = new Random().nextBoolean() ? 0.02F : 0.03F;
        this.particleScale *= 1.5F;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
        if (this.particleAlpha <= this.floatSpeed) {
            this.setExpired();
        } else {
            this.particleAlpha -= this.floatSpeed;
            this.posY = this.posY + this.floatSpeed;
            this.setColor(0.5F + -(this.particleAlpha / 2), 1.0F, 1.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            GobletParticle particle = new GobletParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.selectSpriteRandomly(this.spriteSet);
            return particle;
        }
    }

}
