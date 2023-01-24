package com.vulp.tomes.entities.projectile;

import com.vulp.tomes.init.EffectInit;
import com.vulp.tomes.init.EntityInit;
import com.vulp.tomes.init.ParticleInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerDeathlyIchorParticleMessage;
import com.vulp.tomes.network.messages.ServerWitheringStenchParticleMessage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class DeathlyIchorEntity extends ProjectileEntity {

    private int lifespan = 20;

    public DeathlyIchorEntity(EntityType<? extends DeathlyIchorEntity> entityType, World world) {
        super(entityType, world);
    }

    public DeathlyIchorEntity(World worldIn, PlayerEntity player) {
        this(EntityInit.deathly_ichor, worldIn);
        super.setShooter(player);
        this.setPosition(player.getPosX() - (double)(player.getWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(player.renderYawOffset * ((float)Math.PI / 180F)), player.getPosYEye() - (double)0.1F, player.getPosZ() + (double)(player.getWidth() + 1.0F) * 0.5D * (double)MathHelper.cos(player.renderYawOffset * ((float)Math.PI / 180F)));
        this.lifespan = 20;
    }

    public void tick() {
        super.tick();
        Vector3d vector3d = this.getMotion();
        RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
        if (raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
        }

        double d0 = this.getPosX() + vector3d.x;
        double d1 = this.getPosY() + vector3d.y;
        double d2 = this.getPosZ() + vector3d.z;
        this.updatePitchAndYaw();
        if (this.world.func_234853_a_(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
            this.remove();
        } else if (this.isInWaterOrBubbleColumn()) {
            this.remove();
        } else {
            this.setMotion(vector3d.scale((double) 0.99F));
            if (!this.hasNoGravity()) {
                this.setMotion(this.getMotion().add(0.0D, (double) -0.02F, 0.0D));
            }
            this.setPosition(d0, d1, d2);
        }
        if (this.world.isRemote) {
            float var1 = 0.25F - rand.nextFloat() / 2.0F;
            float var2 = 0.25F - rand.nextFloat() / 2.0F;
            this.world.addParticle(ParticleTypes.PORTAL, this.getPosX() + var1 , this.getPosY() + (rand.nextBoolean() ? var1 : var2) - (this.getHeight() * 2), this.getPosZ() + var2, 0.0D, 0.0D, 0.0D);
        }
        if (this.lifespan > 0) {
            this.lifespan--;
        } else {
            this.remove();
        }
    }

    public int getLifespan() {
        return this.lifespan;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerDeathlyIchorParticleMessage(this.getPosX(), this.getPosY(), this.getPosZ(), this.getEntityId()));
    }

    protected void onEntityHit(EntityRayTraceResult result) {
        super.onEntityHit(result);
        Entity entity = this.getShooter();
        Entity target = result.getEntity();
        if (entity instanceof LivingEntity && target instanceof LivingEntity) {
            LivingEntity livingTarget = (LivingEntity) target;
            if (livingTarget.isEntityUndead()) {
                livingTarget.heal(8.0F);
            } else {
                livingTarget.attackEntityFrom(DamageSource.MAGIC, 8.0F);
            }
        }
    }

    protected void func_230299_a_(BlockRayTraceResult result) {
        super.func_230299_a_(result);
        if (!this.world.isRemote) {
            this.remove();
        }
    }

    protected void registerData() {
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
