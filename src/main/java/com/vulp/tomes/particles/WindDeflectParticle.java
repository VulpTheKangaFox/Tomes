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

public class WindDeflectParticle extends RotationLockedParticle {

    private final IAnimatedSprite spriteWithAge;

    protected WindDeflectParticle(ClientWorld world, double x, double y, double z, double pitch, double yaw, double scale, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z, pitch, yaw, scale);
        this.spriteWithAge = spriteWithAge;
        this.maxAge = 8;
        this.selectSpriteWithAge(spriteWithAge);
    }

    public int getBrightnessForRender(float partialTick) {
        return 15728880;
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.selectSpriteWithAge(this.spriteWithAge);
        }
    }

    public IParticleRenderType getRenderType() {
        return ParticleInit.ParticleTypes.RENDERER_LIT_NOCULL;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WindDeflectParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

}
