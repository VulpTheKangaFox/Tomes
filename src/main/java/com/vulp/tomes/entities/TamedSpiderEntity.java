package com.vulp.tomes.entities;

import com.vulp.tomes.pathfinding.BetterClimberPathNavigator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

public class TamedSpiderEntity extends TameableEntity implements IAngerable {

    private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(TamedSpiderEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> ANGER_TIME = EntityDataManager.createKey(TamedSpiderEntity.class, DataSerializers.VARINT);
    private static final RangedInteger ANGER_TIME_RANGE = TickRangeConverter.convertRange(20, 39);
    private UUID field_234231_bH_;

    public TamedSpiderEntity(EntityType<? extends TameableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new TamedSpiderEntity.AttackGoal(this));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, true));
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 16.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CLIMBING, (byte)0);
        this.dataManager.register(ANGER_TIME, 0);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.writeAngerNBT(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if(!world.isRemote)
            this.readAngerNBT((ServerWorld)this.world, compound);
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return new BetterClimberPathNavigator(this, worldIn);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SPIDER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote) {
            this.setBesideClimbableBlock(this.collidedHorizontally);
            this.func_241359_a_((ServerWorld)this.world, true);
            if (!this.isTamed()) {
                this.remove();
            }
        }
    }

    @Override
    protected boolean isAdult() {
        return true;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    public void setMotionMultiplier(BlockState state, Vector3d motionMultiplierIn) {
        if (!state.matchesBlock(Blocks.COBWEB)) {
            super.setMotionMultiplier(state, motionMultiplierIn);
        }
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        if (potioneffectIn.getPotion() == Effects.POISON) {
            net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(this, potioneffectIn);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    public boolean isBesideClimbableBlock() {
        return (this.dataManager.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.dataManager.get(CLIMBING);
        if (climbing) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.dataManager.set(CLIMBING, b0);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.65F;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            this.setSitting(false);
            if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                amount = (amount + 1.0F) / 2.0F;
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

    @Override
    public void setTamed(boolean tamed) {
        super.setTamed(tamed);
        if (tamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(26.0F);
            this.setHealth(26.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(16.0F);
        }

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0F);
    }

    @Override
    public ActionResultType getEntityInteractionResult(PlayerEntity playerIn, Hand hand) {
        ItemStack itemstack = playerIn.getHeldItem(hand);
        Item item = itemstack.getItem();
        if (this.world.isRemote) {
            boolean flag = this.isOwner(playerIn) || !this.isAngry();
            return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            if (item.isFood() && item.getFood().isMeat() && this.getHealth() < this.getMaxHealth()) {
                if (!playerIn.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
                this.heal((float)item.getFood().getHealing());
                return ActionResultType.SUCCESS;
            }
            ActionResultType actionresulttype = super.getEntityInteractionResult(playerIn, hand);
            if ((!actionresulttype.isSuccessOrConsume() || this.isChild()) && this.isOwner(playerIn)) {
                this.setSitting(!this.isQueuedToSit());
                this.isJumping = false;
                ((BetterClimberPathNavigator)this.navigator).clearPathForSitting();
                this.setAttackTarget(null);
                return ActionResultType.SUCCESS;
            }
            return actionresulttype;
        }
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        /*if (distance > 3) {
            // TODO: Throw some web particles and poofiness for effect when the spider lands.
        }*/
        return super.onLivingFall(distance, 0);
    }

    @Override
    public int getVerticalFaceSpeed() {
        return this.isEntitySleeping() ? 20 : super.getVerticalFaceSpeed();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 0;
    }

    @Override
    public int getAngerTime() {
        return this.dataManager.get(ANGER_TIME);
    }

    @Override
    public void setAngerTime(int time) {
        this.dataManager.set(ANGER_TIME, time);
    }

    @Override
    public void func_230258_H__() {
        this.setAngerTime(ANGER_TIME_RANGE.getRandomWithinRange(this.rand));
    }

    @Nullable
    @Override
    public UUID getAngerTarget() {
        return this.field_234231_bH_;
    }

    @Override
    public void setAngerTarget(@Nullable UUID target) {
        this.field_234231_bH_ = target;
    }

    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        return false;
    }

    @Override
    public boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
            if (target instanceof WolfEntity) {
                WolfEntity wolfentity = (WolfEntity)target;
                return !wolfentity.isTamed() || wolfentity.getOwner() != owner;
            } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).canAttackPlayer((PlayerEntity)target)) {
                return false;
            } else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity)target).isTame()) {
                return false;
            } else {
                return !(target instanceof TameableEntity) || !((TameableEntity)target).isTamed();
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return !this.isAngry() && super.canBeLeashedTo(player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Vector3d getLeashStartPosition() {
        return new Vector3d(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getWidth() * 0.4F));
    }

    @Override
    public void setSitting(boolean p_233687_1_) {
        if (p_233687_1_) {
            this.navigator.updatePath();
        }
        super.setSitting(p_233687_1_);
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld world, AgeableEntity mate) {
        return null;
    }

    static class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(TamedSpiderEntity spider) {
            super(spider, 1.0D, true);
        }

        public boolean shouldExecute() {
            return super.shouldExecute();
        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return (4.0F + attackTarget.getWidth());
        }
    }

}
