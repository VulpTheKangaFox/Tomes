package com.vulp.tomes.entities;

import com.vulp.tomes.init.ParticleInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerOpenHorseInventoryMessage;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

public class SpectralSteedEntity extends HorseEntity {

    private int regenTimer = 35;
    public int lifeTimer;

    public SpectralSteedEntity(EntityType<? extends HorseEntity> type, World worldIn) {
        super(type, worldIn);
        this.lifeTimer = 12000;
        this.setHorseTamed(true);
        this.horseChest.setInventorySlotContents(0, new ItemStack(Items.SADDLE));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorseEntity.class));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
        this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.initExtraAI();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("LifeTime", this.lifeTimer);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("LifeTime")) {
            this.lifeTimer = compound.getInt("LifeTime");
        } else {
            this.lifeTimer = 12000;
        }
    }

    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote) {
            if (this.regenTimer <= 0) {
                this.heal(1.0F);
                this.regenTimer = 35;
            } else {
                this.regenTimer--;
            }

            if (this.lifeTimer <= 0) {
                this.remove();
            } else {
                if (this.lifeTimer <= 100) {
                    // TODO: Add particle as warning that horse will disappear.
                }
                this.lifeTimer--;
            }
        } else {
            this.world.addParticle(ParticleInit.spirit_flame, this.getPosXRandom(0.65D), this.getPosYRandom(), this.getPosZRandom(0.65D), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void openGUI(PlayerEntity player) {
        TomesPacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new ServerOpenHorseInventoryMessage());
    }

    public void saddleUp() {
        this.horseChest.setInventorySlotContents(0, new ItemStack(Items.SADDLE));
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, cause)) return;
        if (!this.removed && !this.dead) {
            Entity entity = cause.getTrueSource();
            LivingEntity livingentity = this.getAttackingEntity();
            if (this.scoreValue >= 0 && livingentity != null) {
                livingentity.awardKillScore(this, this.scoreValue, cause);
            }

            if (this.isSleeping()) {
                this.wakeUp();
            }

            this.dead = true;
            this.getCombatTracker().reset();
            if (this.world instanceof ServerWorld) {
                if (entity != null) {
                    entity.onKillEntity((ServerWorld)this.world, this);
                }

                this.createWitherRose(livingentity);
            }

            this.world.setEntityState(this, (byte)3);
            this.setPose(Pose.DYING);
        }
    }

    @Override
    protected float getModifiedMaxHealth() {
        return 20.0F;
    }

    @Override
    protected double getModifiedJumpStrength() {
        return 0.7D;
    }

    @Override
    protected double getModifiedMovementSpeed() {
        return 0.32D;
    }

    @Override
    public ActionResultType getEntityInteractionResult(PlayerEntity playerIn, Hand hand) {
        if (!this.isChild()) {
            if (!this.isBeingRidden()) {
                this.mountTo(playerIn);
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        return false;
    }

}
