package com.vulp.tomes.entities.projectile;

import com.vulp.tomes.init.EntityInit;
import com.vulp.tomes.init.ParticleInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerTameAnimalMessage;
import com.vulp.tomes.network.messages.ServerWitheringStenchParticleMessage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class WitheringStenchEntity extends ProjectileEntity {

    private int lifespan = 20;

    public WitheringStenchEntity(EntityType<? extends WitheringStenchEntity> entityType, World world) {
        super(entityType, world);
    }

    public WitheringStenchEntity(World worldIn, PlayerEntity player) {
        this(EntityInit.withering_stench, worldIn);
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
                this.setMotion(this.getMotion().add(0.0D, (double) -0.06F, 0.0D));
            }
            this.setPosition(d0, d1, d2);
        }
        if (this.world.isRemote) {
            this.world.addParticle(ParticleInit.withering_stench, this.getPosition().getX(), this.getPosition().getY(), this.getPosZ(), 0.0D, 0.0D, 0.0D);
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
        Vector3d motion = this.getMotion();
        TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerWitheringStenchParticleMessage(this.getPosX(), this.getPosY(), this.getPosZ(), motion.getX(), motion.getY(), motion.getZ()));
        /*if (this.world.isRemote()) {
            for(int i = 0; i < 2; ++i) {
                double d0 = 0.2D + 0.1D * (double)i;
                this.world.addParticle(ParticleInit.withering_stench, this.getPosition().getX() + (rand.nextFloat() * 0.2) - 0.1, this.getPosition().getY() + (rand.nextFloat() * 0.1) - 0.05, this.getPosZ() + (rand.nextFloat() * 0.2) - 0.1, this.getMotion().x * d0, this.getMotion().y, this.getMotion().z * d0);
            }
        }*/
    }

    protected void onEntityHit(EntityRayTraceResult result) {
        super.onEntityHit(result);
        Entity entity = this.getShooter();
        Entity target = result.getEntity();
        if (entity instanceof LivingEntity && target instanceof LivingEntity) {
            ((LivingEntity) target).addPotionEffect(new EffectInstance(Effects.WITHER, 200));
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
