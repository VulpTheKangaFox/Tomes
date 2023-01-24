package com.vulp.tomes.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DeathlyIchorParticle extends SpriteTexturedParticle {

    private final IAnimatedSprite spriteWithAge;
    private final float rotSpeed;
    private final int projID;

    private DeathlyIchorParticle(ClientWorld world, double x, double y, double z, int projID, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z);
        this.multiplyParticleScaleBy(4.0F);
        this.rotSpeed = ((float) Math.random() - 0.5F) * 0.1F;
        this.particleAngle = (float) Math.random() * ((float) Math.PI * 2F);
        this.projID = projID;
        Entity entity = this.world.getEntityByID(projID);
        if (entity != null) {
            this.posX = entity.getPosX();
            this.posY = entity.getPosY() - (entity.getHeight() / 2);
            this.posZ = entity.getPosZ();
        } else {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
        }
        this.maxAge = 20;
        this.particleAlpha = 1.0F;
        this.spriteWithAge = spriteWithAge;
        this.selectSpriteWithAge(spriteWithAge);
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }
        Entity entity = this.world.getEntityByID(this.projID);
        if (entity != null) {
            this.posX = entity.getPosX();
            this.posY = entity.getPosY() + (entity.getHeight() / 2);
            this.posZ = entity.getPosZ();
        }
        this.prevParticleAngle = this.particleAngle;
        this.particleAngle += (float) Math.PI * this.rotSpeed * 2.0F;
        this.particleAlpha = 1.0F - (float)Math.pow(MathHelper.clamp((float)this.age / (float)this.maxAge, 0.0F, 1.0F), 2.0D);
        this.selectSpriteWithAge(this.spriteWithAge);
        this.setBoundingBox(this.getBoundingBox().offset(this.posX - this.prevPosX, this.posY - this.prevPosY, this.posZ - this.prevPosZ));
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DeathlyIchorParticle(worldIn, x, y, z, (int)xSpeed, this.spriteSet);
        }
    }

}