package com.vulp.tomes.particles;

import com.vulp.tomes.init.ParticleInit;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WebNetParticle extends RotationLockedParticle{

    protected WebNetParticle(ClientWorld world, double x, double y, double z) {
        this(world, x, y, z, 1.0F);
    }

    protected WebNetParticle(ClientWorld world, double x, double y, double z, double scale) {
        super(world, x, y, z, 90.0F, 0.0F, scale);
        this.maxAge = 50;
        this.particleAlpha = 1.0F;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            if (this.particleAlpha <= 0.02F) {
                this.setExpired();
            } else {
                this.particleAlpha = this.particleAlpha - 0.02F;
            }
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return ParticleInit.ParticleTypes.RENDERER_TRANSPARENT_NOCULL;
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
            WebNetParticle particle = new WebNetParticle(worldIn, x, y, z, zSpeed);
            particle.selectSpriteRandomly(this.spriteSet);
            return particle;
        }
    }

}
