package com.vulp.tomes.entities;

import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerMindBendMessage;
import com.vulp.tomes.network.messages.ServerWitchOfCogencyCastMessage;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@OnlyIn(
        value = Dist.CLIENT,
        _interface = IChargeableMob.class
)
public class WitchOfCogencyEntity extends MonsterEntity implements IRangedAttackMob, IChargeableMob {
    private static final DataParameter<Integer> INVULNERABILITY_TIME = EntityDataManager.createKey(WitchOfCogencyEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> FIRST_STAGE = EntityDataManager.createKey(WitchOfCogencyEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_USING = EntityDataManager.createKey(WitchOfCogencyEntity.class, DataSerializers.BOOLEAN);
    private static final UUID MODIFIER_UUID = UUID.fromString("8B4663E9-2A0B-485A-83DD-6CBF578E62EA");
    private static final AttributeModifier MODIFIER = new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.45D, AttributeModifier.Operation.ADDITION);
    private final EntityPredicate ownedVexPredicate = (new EntityPredicate()).setDistance(16.0D).setIgnoresLineOfSight().setUseInvisibilityCheck().allowInvulnerable().allowFriendlyFire();
    private final ServerBossInfo bossInfo1 = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_12)).setDarkenSky(false).setCreateFog(true);
    private final ServerBossInfo bossInfo2 = (ServerBossInfo)(new ServerBossInfo(new StringTextComponent(this.getDisplayName().getString() + new TranslationTextComponent("entity.tomes.witch_of_cogency_stage_2").getString()).mergeStyle(TextFormatting.RED, TextFormatting.BOLD), BossInfo.Color.PINK, BossInfo.Overlay.NOTCHED_6)).setDarkenSky(true).setCreateFog(true);
    private int potionUseTimer;
    private int teleportUrgency = 0;

    @OnlyIn(Dist.CLIENT)
    private boolean isCasting = false;

    public WitchOfCogencyEntity(EntityType<? extends WitchOfCogencyEntity> entityType, World world) {
        super(entityType, world);
        this.setHealth(this.getTrueMaxHealth());
        this.getNavigator().setCanSwim(true);
        this.experienceValue = 50;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SpecialRangedAttackGoal(this, 1.0D, 60, 20, 10.0F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, MobEntity.class, 0, false, false, entity -> !(entity instanceof MonsterEntity)));
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(INVULNERABILITY_TIME, 0);
        this.dataManager.register(FIRST_STAGE, true);
        this.dataManager.register(IS_USING, false);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Invul", this.getInvulTime());
        compound.putBoolean("FirstStg", this.isFirstStage());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setInvulTime(compound.getInt("Invul"));
        this.setFirstStage(compound.getBoolean("FirstStg"));
        if (this.hasCustomName()) {
            this.bossInfo1.setName(this.getDisplayName());
            this.bossInfo2.setName(this.getDisplayName());
        }
        boolean stage1 = this.isFirstStage();
        this.bossInfo1.setVisible(stage1);
        this.bossInfo2.setVisible(!stage1);
    }

    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        this.bossInfo1.setName(this.getDisplayName());
        this.bossInfo2.setName(this.getDisplayName());
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITCH_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_WITCH_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITCH_DEATH;
    }

    public void livingTick() {
        if (this.getInvulTime() <= 0) {
            if (!this.world.isRemote && this.isAlive()) {
                if (this.isUsingItem()) {
                    if (this.potionUseTimer-- <= 0) {
                        this.setUsingItem(false);
                        ItemStack itemstack = this.getHeldItemMainhand();
                        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                        if (itemstack.getItem() == Items.POTION) {
                            List<EffectInstance> list = PotionUtils.getEffectsFromStack(itemstack);
                            for (EffectInstance effectinstance : list) {
                                this.addPotionEffect(new EffectInstance(effectinstance));
                            }
                        } else {
                            if (itemstack.getItem() == Items.GOLDEN_APPLE) {
                                this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 160, 2));
                                this.heal(8.0F);
                            }
                            if (itemstack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                                this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 160, 3));
                                this.heal(12.0F);
                            }
                            if (itemstack.getItem() == Items.ENDER_PEARL) {
                                boolean flag = false;
                                for (int i = 0; i < 5; i++) {
                                    if (this.teleportRandomly()) {
                                        flag = true;
                                        break;
                                    }
                                }
                                if (flag) {
                                    this.teleportUrgency = 0;
                                    this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                                }
                            }
                            if (itemstack.getItem() == Items.MILK_BUCKET) {
                                this.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
                            }
                        }
                        this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(MODIFIER);
                    }
                } else {
                    ItemStack useItem = null;
                    boolean fallFlag = false;
                    if (this.fallDistance > 2 && !this.isPotionActive(Effects.SLOW_FALLING)) {
                        useItem = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.SLOW_FALLING);
                        fallFlag = true;
                    } else if (this.rand.nextFloat() < 0.15F && this.areEyesInFluid(FluidTags.WATER) && !this.isPotionActive(Effects.WATER_BREATHING)) {
                        useItem = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER_BREATHING);
                    } else if (this.rand.nextFloat() < 0.15F && (this.isBurning() || this.getLastDamageSource() != null && this.getLastDamageSource().isFireDamage()) && !this.isPotionActive(Effects.FIRE_RESISTANCE)) {
                        useItem = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.FIRE_RESISTANCE);
                    } else if (this.getActivePotionEffects().stream().anyMatch(e -> e.getPotion().getEffectType() == EffectType.HARMFUL)) {
                        useItem = new ItemStack(Items.MILK_BUCKET);
                    } else if (this.rand.nextFloat() < 0.01F && this.getHealth() < this.getTrueMaxHealth()) {
                        useItem = this.isFirstStage() ? new ItemStack(Items.GOLDEN_APPLE) : new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
                    } else if (!this.isFirstStage() && this.needsTeleport()) {
                        useItem = new ItemStack(Items.ENDER_PEARL);
                    } else if (this.rand.nextFloat() < 0.5F && this.getAttackTarget() != null && !this.isPotionActive(Effects.SPEED) && this.getAttackTarget().getDistanceSq(this) > 121.0D) {
                        useItem = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.SWIFTNESS);
                    }

                    if (useItem != null) {
                        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, useItem);
                        this.potionUseTimer = fallFlag ? 10 : this.getHeldItemMainhand().getUseDuration();
                        this.setUsingItem(true);
                        if (!this.isSilent()) {
                            Item item = useItem.getItem();
                            if (item instanceof PotionItem || item == Items.MILK_BUCKET) {
                                this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                            } else {
                                if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
                                    this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_GENERIC_EAT, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                                }
                            }
                        }
                        ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                        modifiableattributeinstance.removeModifier(MODIFIER);
                        modifiableattributeinstance.applyNonPersistentModifier(MODIFIER);
                    }
                }

                if (this.rand.nextFloat() < 7.5E-4F) {
                    this.world.setEntityState(this, (byte) 15);
                }
            }

        } else {
            if (this.world.isRemote()) {
                for (int i1 = 0; i1 < 3; ++i1) {
                    this.world.addParticle(ParticleTypes.WITCH, this.getPosX() - 0.5F + rand.nextFloat(), this.getPosY() + rand.nextFloat() * 2, this.getPosZ() - 0.5F + rand.nextFloat(), (double) 0.7F, (double) 0.7F, (double) 0.9F);
                }
            }
        }

        super.livingTick();

    }

    protected void updateAITasks() {
        if (this.getInvulTime() > 0) {
            int j1 = this.getInvulTime() - 1;
            if (j1 <= 0) {
                if (!this.isSilent()) {
                    this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_WITCH_CELEBRATE, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                }
            }
            this.getLookController().setLookPosition(0.0D, 0.0D, 0.0D);
            this.getMoveHelper().setMoveTo(0.0D, 0.0D, 0.0D, 0.0D);
            this.setInvulTime(j1);
            this.heal(this.isFirstStage() ? 2.0F : 1.0F);
        } else {
            super.updateAITasks();
            if (this.getAttackTarget() != null && !this.hasPath()) {
                this.teleportUrgency++;
            }
        }
        if (this.isFirstStage()) {
            this.bossInfo1.setPercent(this.getHealth() / this.getTrueMaxHealth());
        } else {
            this.bossInfo2.setPercent(this.getHealth() / this.getTrueMaxHealth());
        }
    }

    public float getFallSpeed() {
        return (float) MathHelper.clamp(this.prevPosY - this.getPosY(), 0.0F, 100.0F);
    }

    private boolean needsTeleport() {
        return this.teleportUrgency > 119;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() instanceof PotionEntity) {
            PotionEntity potion = (PotionEntity) source.getTrueSource();
            if (potion.getShooter() == this) {
                return false;
            }
        }
        if (this.world.isRemote) {
            return super.attackEntityFrom(source, amount);
        }
        if (this.getInvulTime() <= 0) {
            if (this.isFirstStage()) {
                if (amount >= this.getHealth()) {
                    this.transform();
                    return super.attackEntityFrom(source, 0.0F);
                } else {
                    return super.attackEntityFrom(source, amount);
                }
            }
            if (this.rand.nextInt(10) == 0) {
                this.teleportUrgency = 120;
            }
            return super.attackEntityFrom(source, amount);
        }
        return false;
    }

    @Override
    public void onKillCommand() {
        this.setFirstStage(false);
        super.onKillCommand();
    }

    protected boolean teleportRandomly() {
        if (!this.world.isRemote() && this.isAlive()) {
            double d0 = this.getPosX() + (this.rand.nextDouble() - 0.5D) * 32.0D;
            double d1 = this.getPosY() + (double)(this.rand.nextInt(12) - 6);
            double d2 = this.getPosZ() + (this.rand.nextDouble() - 0.5D) * 32.0D;
            return this.teleportTo(d0, d1, d2);
        } else {
            return false;
        }
    }

    private boolean teleportTo(double x, double y, double z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

        while(mutable.getY() > 0 && !this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockstate = this.world.getBlockState(mutable);
        boolean flag = blockstate.getMaterial().blocksMovement();
        boolean flag1 = blockstate.getFluidState().isTagged(FluidTags.WATER);
        if (flag && !flag1) {
            net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this, x, y, z, 0);
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
            boolean flag2 = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2 && !this.isSilent()) {
                this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
        } else {
            return false;
        }
    }



    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        summon();
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    // Intro sequence
    public void summon() {
        this.setHealth(1.0F);
        this.setFirstStage(true);
        this.setIdleTime(150);
        this.setInvulTime(150);
        this.bossInfo1.setVisible(true);
        this.bossInfo2.setVisible(false);
    }

    // Shift to stage 2
    public void transform() {
        this.setHealth(1.0F);
        this.setFirstStage(false);
        this.setIdleTime(150);
        this.setInvulTime(150);
        this.bossInfo1.setVisible(false);
        this.bossInfo2.setVisible(true);
    }

    public void addTrackingPlayer(ServerPlayerEntity player) {
        super.addTrackingPlayer(player);
        this.bossInfo1.addPlayer(player);
        this.bossInfo2.addPlayer(player);
    }

    public void removeTrackingPlayer(ServerPlayerEntity player) {
        super.removeTrackingPlayer(player);
        this.bossInfo1.removePlayer(player);
        this.bossInfo2.removePlayer(player);
    }

    @Override
    public boolean attackable() {
        return this.getInvulTime() <= 0;
    }

    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        if (!this.isUsingItem() && this.getInvulTime() <= 0) {
            if (this.isFirstStage()) {
                Vector3d vector3d = target.getMotion();
                double d0 = target.getPosX() + vector3d.x - this.getPosX();
                double d1 = target.getPosYEye() - (double) 1.1F - this.getPosY();
                double d2 = target.getPosZ() + vector3d.z - this.getPosZ();
                float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
                Potion potion = Potions.STRONG_HARMING;
                if (this.rand.nextInt(4) == 0 && target.getHealth() >= 8.0F && !target.isPotionActive(Effects.POISON)) {
                    potion = Potions.POISON;
                } else if (this.rand.nextInt(4) == 0 && this.world.getTargettableEntitiesWithinAABB(VexEntity.class, this.ownedVexPredicate, this, this.getBoundingBox().grow(16.0D)).size() < 4) {
                    potion = null;
                }

                if (potion == null) {
                    ServerWorld serverWorld = (ServerWorld) this.world;
                    BlockPos blockpos = this.getPosition().add(-2 + this.rand.nextInt(5), 1, -2 + this.rand.nextInt(5));
                    VexEntity vexentity = EntityType.VEX.create(this.world);
                    if (vexentity != null) {
                        vexentity.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
                        vexentity.onInitialSpawn(serverWorld, this.world.getDifficultyForLocation(blockpos), SpawnReason.MOB_SUMMONED, null, null);
                        vexentity.setOwner(this);
                        vexentity.setBoundOrigin(blockpos);
                        vexentity.setLimitedLife(20 * (30 + this.rand.nextInt(90)));
                        serverWorld.func_242417_l(vexentity);
                        this.playSound(SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, 1.0F, 1.0F);
                    }
                } else {
                    PotionEntity potionentity = new PotionEntity(this.world, this);
                    potionentity.setItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potion));
                    potionentity.rotationPitch += MathHelper.clamp(target.getDistance(this) * 6, 0, 20);
                    potionentity.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);
                    if (!this.isSilent()) {
                        this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                    }
                    this.world.addEntity(potionentity);
                }
            } else {
                if (!this.isSilent()) {
                    this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_WITHER_SHOOT, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                }
                double d3 = target.getPosX() - this.getPosX();
                double d4 = (target.getPosY() + (target.getEyeHeight() * 0.5D)) - (this.getPosY() + (this.getHeight() * 0.5F));
                double d5 = target.getPosZ() - this.getPosZ();
                WitherBallEntity witherBall = new WitherBallEntity(this.world, this, d3, d4, d5);
                witherBall.setShooter(this);
                witherBall.setRawPosition(this.getPosX() + getLookVec().getX(), this.getPosY() + (this.getHeight() * 0.5F) + getLookVec().getY(), this.getPosZ() + getLookVec().getZ());
                this.world.addEntity(witherBall);
            }
        }
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        if (entityIn == null) {
            return false;
        } else if (super.isOnSameTeam(entityIn)) {
            return true;
        } else if (entityIn instanceof VexEntity) {
            return this.isOnSameTeam(((VexEntity)entityIn).getOwner());
        } else {
            return false;
        }
    }

    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        ItemEntity itementity1 = this.entityDropItem(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(EnchantmentInit.TREASURE_TOME_ENCHANTS.get(this.rand.nextInt(EnchantmentInit.TREASURE_TOME_ENCHANTS.size())), 1)));
        if (itementity1 != null) {
            itementity1.setNoDespawn();
        }

        if (new Random().nextInt(3) == 0) {
            ItemEntity itementity2 = this.entityDropItem(ItemInit.amulet_of_cogency);
            if (itementity2 != null) {
                itementity2.setNoDespawn();
            }
        }
        super.dropSpecialItems(source, looting, recentlyHitIn);
    }

    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDespawnPeaceful()) {
            this.remove();
        } else {
            this.idleTime = 0;
        }
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 300.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D).createMutableAttribute(Attributes.FOLLOW_RANGE, 40.0D).createMutableAttribute(Attributes.ARMOR, 2.0D);
    }

    public float getTrueMaxHealth() {
        return this.getMaxHealth() * (this.isFirstStage() ? 1.0F : 0.5F);
    }

    public int getInvulTime() {
        return this.dataManager.get(INVULNERABILITY_TIME);
    }

    public void setInvulTime(int time) {
        this.dataManager.set(INVULNERABILITY_TIME, time);
    }

    public boolean isFirstStage() {
        return this.dataManager.get(FIRST_STAGE);
    }

    public void setFirstStage(boolean firstStage) {
        this.dataManager.set(FIRST_STAGE, firstStage);
    }

    public void setUsingItem(boolean drinkingPotion) {
        this.getDataManager().set(IS_USING, drinkingPotion);
    }

    public boolean isUsingItem() {
        return this.getDataManager().get(IS_USING);
    }

    protected float applyPotionDamageCalculations(DamageSource source, float damage) {
        damage = super.applyPotionDamageCalculations(source, damage);
        if (source.getTrueSource() == this) {
            damage = 0.0F;
        }

        if (source.isMagicDamage()) {
            damage = (float)((double)damage * 0.15D);
        }

        return damage;
    }

    public boolean isCharged() {
        return getInvulTime() > 0;
    }

    public boolean canChangeDimension() {
        return false;
    }

    private class SpecialRangedAttackGoal extends RangedAttackGoal {

        private final WitchOfCogencyEntity witch;
        private final int attackSpeed2;
        private boolean wasExecuting = false;

        private SpecialRangedAttackGoal(WitchOfCogencyEntity witch, double moveSpeed, int attackSpeed1, int attackSpeed2, float attackDistance) {
            super(witch, moveSpeed, attackSpeed1, attackDistance);
            this.witch = witch;
            this.attackSpeed2 = attackSpeed2;
        }

        @Override
        public boolean shouldExecute() {
            boolean flag1 = this.wasExecuting;
            boolean flag2 = super.shouldExecute();
            if (flag1 != flag2) {
                TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerWitchOfCogencyCastMessage(this.witch.getEntityId(), flag2));
            }
            this.wasExecuting = flag2;
            return flag2;
        }

        /*@Override
        public boolean shouldContinueExecuting() {
            boolean flag = super.shouldContinueExecuting();
            this.wasExecuting = flag;
            return flag;
        }
*/
        @Override
        public void tick() {
            if (!this.witch.isFirstStage()) {
                this.attackIntervalMin = this.attackSpeed2;
                this.maxRangedAttackTime = this.attackSpeed2;
            }
            super.tick();
        }

    }

    public void setCasting(boolean casting) {
        this.isCasting = casting;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isCasting() {
        return this.isCasting;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldFoldArms() {
        return this.isFirstStage() || this.isUsingItem();
    }

}
