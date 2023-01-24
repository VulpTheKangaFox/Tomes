package com.vulp.tomes.entities;

import com.vulp.tomes.init.EntityInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class WitherBallEntity extends DamagingProjectileEntity {

    public WitherBallEntity(EntityType<? extends WitherBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public WitherBallEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(EntityInit.wither_ball, shooter, accelX, accelY, accelZ, worldIn);
    }

    @OnlyIn(Dist.CLIENT)
    public WitherBallEntity(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(EntityInit.wither_ball, x, y, z, accelX, accelY, accelZ, worldIn);
    }

    protected float getMotionFactor() {
        return 0.73F;
    }

    public boolean isBurning() {
        return false;
    }

    protected void onEntityHit(EntityRayTraceResult result) {
        super.onEntityHit(result);
        if (!this.world.isRemote) {
            Entity entity = result.getEntity();
            if (entity.attackEntityFrom(DamageSource.MAGIC, 5.0F) && entity instanceof LivingEntity) {
                int i = 2;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    i = 12;
                } else if (this.world.getDifficulty() == Difficulty.HARD) {
                    i = 30;
                }
                ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.WITHER, 20 * i, 1));
            }

        }
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote) {
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 1.0F, false, Explosion.Mode.NONE);
            this.remove();
        }

    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith() {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    protected boolean isFireballFiery() {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}