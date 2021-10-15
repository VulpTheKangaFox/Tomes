package com.vulp.tomes.entities;

import com.vulp.tomes.init.ParticleInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class WildWolfEntity extends WolfEntity {

    private int regenTimer = 35;
    public int lifeTimer;

    private boolean fade = false;
    private int fadeTimer = 14;
    private int fadeTimerMax = 14;

    public WildWolfEntity(EntityType<? extends WolfEntity> type, World worldIn) {
        super(type, worldIn);
        this.lifeTimer = 2400;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, true));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("LifeTime", this.lifeTimer);
        this.writeAngerNBT(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("LifeTime")) {
            this.lifeTimer = compound.getInt("LifeTime");
        } else {
            this.lifeTimer = 2400;
        }
        if(!world.isRemote) {
            this.readAngerNBT((ServerWorld) this.world, compound);
        }
    }

    public void tick() {
        super.tick();
        boolean flag = false;
        if (!this.world.isRemote) {
            if (this.regenTimer <= 0) {
                this.heal(1.0F);
                this.regenTimer = 35;
            } else {
                this.regenTimer--;
            }
        } else {
            flag = true;
        }
        if (this.lifeTimer <= 0) {
            this.remove();
        } else {
            if (this.lifeTimer <= 150 && flag) {
                if (this.fadeTimer <= 0) {
                    this.fade = !this.fade;
                    this.fadeTimer = this.fadeTimerMax;
                    if (this.fadeTimerMax > 2) {
                        this.fadeTimerMax--;
                    }
                } else {
                    this.fadeTimer--;
                }
                this.world.addParticle(ParticleInit.wild_wolf_despawn, this.getPosX(), this.getPosY() + (this.getHeight() / 2.0F), this.getPosZ(), this.getEntityId(), 0.0F, 0.0F);
            }
            this.lifeTimer--;
        }

    }

    public ActionResultType getEntityInteractionResult(PlayerEntity playerIn, Hand hand) {
        if (this.world.isRemote) {
            boolean flag = this.isOwner(playerIn);
            return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            if (this.isTamed()) {
                ActionResultType actionresulttype = super.getEntityInteractionResult(playerIn, hand);
                if ((!actionresulttype.isSuccessOrConsume() || this.isChild()) && this.isOwner(playerIn)) {
                    this.setSitting(!this.isQueuedToSit());
                    this.isJumping = false;
                    this.navigator.clearPath();
                    this.setAttackTarget(null);
                    return ActionResultType.SUCCESS;
                }

                return actionresulttype;
            }
            return super.getEntityInteractionResult(playerIn, hand);
        }
    }

    public boolean getFade() {
        return this.fade;
    }

}
