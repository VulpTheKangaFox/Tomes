package com.vulp.tomes.events;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.effects.MultiJumpEffect;
import com.vulp.tomes.entities.WitchOfCogencyEntity;
import com.vulp.tomes.entities.ai.MindBendFollowGoal;
import com.vulp.tomes.entities.ai.NullifyAttackableTargetGoal;
import com.vulp.tomes.init.*;
import com.vulp.tomes.inventory.container.WitchMerchantContainer;
import com.vulp.tomes.items.DebugItem;
import com.vulp.tomes.items.TomeItem;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerCropBreakMessage;
import com.vulp.tomes.network.messages.ServerMindBendMessage;
import com.vulp.tomes.network.messages.ServerProjDeflectMessage;
import com.vulp.tomes.util.HealthHandler;
import com.vulp.tomes.util.SpellEnchantUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.*;

@Mod.EventBusSubscriber(modid=Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

    private static final HashMap<LivingEntity, Integer> mindbend_particle_handlers = new HashMap<>();
    public static final HashMap<LivingEntity, Integer> poison_resistance_toggle = new HashMap<>();
    public static final HashMap<LivingEntity, Integer> hunger_resistance_toggle = new HashMap<>();
    private static final List<Pair<LivingEntity, HealthHandler>> health_handlers = new ArrayList<>();
    private static int slow_tick_timer = 10;

    @SubscribeEvent
    public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
        Entity player = event.getTarget();
        Entity aggressor = event.getEntity();
        if (event.getTarget() instanceof PlayerEntity) {
            if (TomesConfig.covens_rule_enabled.get()) {
                if (event.getEntity() instanceof WitchEntity) {
                    if (SpellEnchantUtil.hasEnchant((PlayerEntity) player, EnchantmentInit.covens_rule)) {
                        ((WitchEntity) aggressor).setAttackTarget(null);
                    }
                }
            }
            if (TomesConfig.covens_rule_enabled.get()) {
                if (aggressor instanceof SpiderEntity || aggressor instanceof CreeperEntity || aggressor instanceof ZombieEntity || aggressor instanceof SkeletonEntity) {
                    if (SpellEnchantUtil.hasEnchant((PlayerEntity) player, EnchantmentInit.rotten_heart)) {
                        ((MobEntity) aggressor).setAttackTarget(null);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDropsEvent(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        Random rand = new Random();
        if (entity instanceof WitchEntity) {
            if (rand.nextInt(99) < TomesConfig.sweet_heart_droprate.get()) {
                event.getDrops().add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY() + entity.getYOffset(), entity.getPosZ(), new ItemStack(ItemInit.sweet_heart)));
            }
        } else if (entity instanceof VillagerEntity || entity instanceof AbstractIllagerEntity) {
            if (rand.nextInt(99) < TomesConfig.beating_heart_droprate.get()) {
                event.getDrops().add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY() + entity.getYOffset(), entity.getPosZ(), new ItemStack(ItemInit.beating_heart)));
            }
        } else if (entity instanceof ZombieEntity) {
            if (rand.nextInt(99) < TomesConfig.archaic_heart_droprate.get()) {
                event.getDrops().add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY() + entity.getYOffset(), entity.getPosZ(), new ItemStack(ItemInit.ancient_heart)));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDespawnEvent(LivingSpawnEvent.AllowDespawn event) {
        Entity entity = event.getEntity();
        if (entity instanceof WitchEntity && entity.getPersistentData().contains("Offers")) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPotionExpiry(PotionEvent.PotionExpiryEvent event) {
        if (event.getPotionEffect() != null && event.getPotionEffect().getPotion() == EffectInit.antidotal) {
            EntityEvents.poison_resistance_toggle.remove(event.getEntityLiving());
            EntityEvents.hunger_resistance_toggle.remove(event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public static void onPotionRemoved(PotionEvent.PotionRemoveEvent event) {
        EffectInstance instance = event.getPotionEffect();
        if (instance != null && (instance.getPotion() == EffectInit.adrenal_recharge || instance.getPotion() == EffectInit.tenacity_recharge)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelable() && event.getPlayer().isPotionActive(EffectInit.starry_form)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        PlayerEntity player = event.getPlayer();
        Entity entity = event.getTarget();
        World world = player.world;
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        if (item instanceof TomeItem) {
            if (TomesConfig.beast_tamer_enabled.get() && entity instanceof AbstractHorseEntity) {
                if (SpellEnchantUtil.hasEnchant(player, EnchantmentInit.beast_tamer)) {
                    Hand hand = Hand.OFF_HAND;
                    if (stack == player.getHeldItemMainhand()) {
                        hand = Hand.MAIN_HAND;
                    }
                    item.itemInteractionForEntity(stack, player, (LivingEntity) entity, hand);
                    item.onItemRightClick(world, player, hand);
                }
            } else if (TomesConfig.covens_rule_enabled.get() && entity instanceof WitchEntity && SpellEnchantUtil.hasEnchant(player, EnchantmentInit.covens_rule)) {
                MerchantOffers offers = WitchMerchantContainer.getMerchantOffers(entity);

                if (player.getActiveHand() == Hand.MAIN_HAND) {
                    player.addStat(Stats.TALKED_TO_VILLAGER);
                }

                if (!world.isRemote && !offers.isEmpty()) {
                    CompoundNBT nbt = entity.getPersistentData();
                    nbt.putUniqueId("Customer", player.getUniqueID());
                    OptionalInt optionalint = player.openContainer(new SimpleNamedContainerProvider((id, playerInventory, player2) -> new WitchMerchantContainer(id, playerInventory, (WitchEntity) entity), entity.getDisplayName()));
                    if (optionalint.isPresent()) {
                        MerchantOffers merchantoffers = WitchMerchantContainer.getMerchantOffers(entity);
                        if (!merchantoffers.isEmpty()) {
                            player.openMerchantContainer(optionalint.getAsInt(), merchantoffers, 0, 0, false, false);
                        }
                    }
                }
            }
        }
        if (item instanceof DebugItem) {
            ((DebugItem)item).debugEntity(world, entity, stack);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        if (TomesConfig.advantageous_growth_enabled.get() && SpellEnchantUtil.hasEnchant(player, EnchantmentInit.advantageous_growth) && state.getBlock() instanceof CropsBlock && ((CropsBlock) state.getBlock()).isMaxAge(state) && !player.isCreative()) {
            Block.spawnDrops(state, world, pos);
            TomesPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with((() -> (Chunk) world.getChunk(pos))), new ServerCropBreakMessage(pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    @SubscribeEvent
    public static void entityDamagedEvent(LivingDamageEvent event) {
        Entity attacker = event.getSource().getTrueSource();
        if (attacker instanceof LivingEntity && ((LivingEntity) attacker).isPotionActive(EffectInit.fire_fist)) {
            event.getEntityLiving().setFire(20);
        }
        LivingEntity victim = event.getEntityLiving();
        /*if (victim instanceof VexEntity) {
            if (attacker instanceof WitchOfCogencyEntity) {
                if (((VexEntity) victim).getOwner() == attacker) {
                    event.setCanceled(true);
                }
            }
        }
        if (attacker instanceof WitchOfCogencyEntity && victim instanceof VexEntity && ((VexEntity) victim).getOwner() == attacker) {
            event.setCanceled(true);
        }*/
        if (SpellEnchantUtil.hasEnchant(victim, EnchantmentInit.fight_or_flight)) {
            if ((victim.getHealth() - event.getAmount()) / victim.getMaxHealth() <= 0.3F && !victim.isPotionActive(EffectInit.adrenal_recharge)) {
                victim.addPotionEffect(new EffectInstance(Effects.SPEED, 400, 1));
                victim.addPotionEffect(new EffectInstance(Effects.STRENGTH, 400, 1));
                victim.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 400, 0));
                victim.setAbsorptionAmount(4.0F);
                victim.addPotionEffect(new EffectInstance(EffectInit.adrenal_recharge, TomesConfig.fight_or_flight_cooldown.get(), 0, true, false));
            }
        }
    }

    @SubscribeEvent
    public static void entityAttackedEvent(LivingAttackEvent event) {
        Entity attacker = event.getSource().getTrueSource();
        LivingEntity victim = event.getEntityLiving();
        if (attacker instanceof WitchOfCogencyEntity && victim instanceof VexEntity && ((VexEntity) victim).getOwner() == attacker) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void projHitEvent(ProjectileImpactEvent event) {
        if (TomesConfig.airy_protection_enabled.get()) {
            Entity proj = event.getEntity();
            if (proj != null) {
                Vector3d vector3d1 = proj.getPositionVec();
                Vector3d vector3d2 = vector3d1.add(proj.getMotion());
                RayTraceResult result = event.getRayTraceResult();
                EntityRayTraceResult raytrace = ProjectileHelper.rayTraceEntities(event.getEntity().world, proj, vector3d1, vector3d2, proj.getBoundingBox().expand(proj.getMotion()).grow(1.0D), i -> true);
                if (result != null && raytrace != null && result.getType() == RayTraceResult.Type.ENTITY) {
                    Entity victim = raytrace.getEntity();
                    if (victim instanceof PlayerEntity && SpellEnchantUtil.hasEnchant((PlayerEntity) victim, EnchantmentInit.airy_protection) && proj instanceof ProjectileEntity && new Random().nextBoolean()) {
                        if (!proj.world.isRemote) {
                            TomesPacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> victim), new ServerProjDeflectMessage(victim.getEntityId(), proj.getEntityId()));
                            Vector3d vec = proj.getMotion();
                            Vector3d vec2 = vec.inverse();
                            try {
                                proj.setMotion(vec2.getX() * 0.75, vec2.getY() * 0.75, vec2.getZ() * 0.75);
                            } catch (NoSuchMethodError error) {
                                Tomes.LOGGER.debug("Caught an error typically caused by using multiple projectile deflection methods."); // I actually have no idea if this works.
                                return;
                            }
                            proj.rotationPitch = MathHelper.wrapDegrees(proj.rotationPitch + 180);
                            proj.rotationYaw = MathHelper.wrapDegrees(proj.rotationYaw + 180);
                            if (proj instanceof DamagingProjectileEntity) {
                                DamagingProjectileEntity damageProj = (DamagingProjectileEntity) proj;
                                damageProj.accelerationX = -damageProj.accelerationX * 0.75;
                                damageProj.accelerationY = -damageProj.accelerationY * 0.75;
                                damageProj.accelerationZ = -damageProj.accelerationZ * 0.75;
                            }
                            ((ProjectileEntity) proj).setShooter(victim);
                            proj.velocityChanged = true;
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFinishItemUseEvent(LivingEntityUseItemEvent.Finish event) {
        Item item = event.getItem().getItem();
        LivingEntity entity = event.getEntityLiving();
        if (item.isFood() && entity instanceof PlayerEntity && item == ItemInit.ancient_heart) {
            ((PlayerEntity) entity).giveExperiencePoints(20);
        }
    }

    @SubscribeEvent
    public static void onLivingHealed(LivingHealEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.isPotionActive(EffectInit.leaden_veins)) {
            float amount = event.getAmount();
            if (amount > 2.0F) {
                event.setAmount(amount - 2.0F);
            }
            else if (amount > 1.0F) {
                event.setAmount(amount - 1.0F);
            } else {
                boolean flag = false;
                Optional<Pair<LivingEntity, HealthHandler>> optional = health_handlers.stream().filter(pair -> pair.getFirst() == entity).findFirst();
                if (optional.isPresent()) {
                    HealthHandler handler = optional.get().getSecond();
                    if (handler != null) {
                        if (handler.shouldHeal()) {
                            event.setAmount(amount);
                        } else {
                            event.setAmount(0.0F);
                        }
                        handler.toggleHeal();
                    } else {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
                if (flag) {
                    health_handlers.add(new Pair<>(entity, new HealthHandler(entity)));
                    event.setAmount(0.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (SpellEnchantUtil.hasEnchant(entity, EnchantmentInit.borrowed_time) && !entity.isPotionActive(EffectInit.tenacity_recharge)) {
            entity.setHealth(entity.getMaxHealth());
            entity.addPotionEffect(new EffectInstance(Effects.WITHER, 200, 2));
            entity.addPotionEffect(new EffectInstance(EffectInit.tenacity_recharge, TomesConfig.borrowed_time_cooldown.get(), 2));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void livingTickEvent(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity != null && entity.getPersistentData().contains("HexParticle")) {
            CompoundNBT nbt = entity.getPersistentData();
            if (entity.world.isRemote && nbt.getBoolean("HexParticle")) {
                mindbend_particle_handlers.putIfAbsent(entity, 7);
            } else {
                if (slow_tick_timer == 0) {
                    if (entity instanceof MobEntity && nbt.hasUniqueId("PlayerFollowingUUID") && entity.isPotionActive(EffectInit.mind_bend)) {
                        MobEntity mobEntity = (MobEntity) entity;
                        if (mobEntity.goalSelector.goals.stream().noneMatch(pGoal -> pGoal.getGoal() instanceof MindBendFollowGoal)) {
                            mobEntity.goalSelector.addGoal(1, new MindBendFollowGoal(mobEntity, 1.0F, 5.0F, 2.0F));
                            mobEntity.targetSelector.addGoal(0, new NullifyAttackableTargetGoal(mobEntity, false));
                        }
                    }
                }
                TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerMindBendMessage(entity.getEntityId(), nbt.getBoolean("HexParticle")));
            }
        }
    }

    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.player.isPotionActive(EffectInit.starry_form)) {
            if (event.phase == TickEvent.Phase.START) {
                event.player.setOnGround(false);
                event.player.abilities.isFlying = true;
            } else {
                event.player.setPose(Pose.SWIMMING);
            }
        }
    }

    @SubscribeEvent
    public static void entityLeaveWorldEvent(EntityLeaveWorldEvent event) {
        mindbend_particle_handlers.clear();
    }

    @SubscribeEvent
    public static void serverTickEvent(TickEvent.ServerTickEvent event) {
        // Ticks the health handlers regarding the leaden veins effect.
        if (!health_handlers.isEmpty()) {
            health_handlers.removeIf(pair -> pair.getFirst() == null || pair.getSecond() == null || pair.getSecond().readyToRemove());
            health_handlers.forEach(pair -> pair.getSecond().tick());
        }
        /*// Reinitializes AI for the mind bend effect if it is missing.
        List<LivingEntity> removalList = new ArrayList<>(Collections.emptyList());
        if (!MindBenderSpell.mindBendMap.isEmpty()) {
            MindBenderSpell.mindBendMap.forEach((entity, player) -> {
                if (entity instanceof MobEntity && !entity.removed) {
                    for (Goal goal : ((MobEntity) entity).goalSelector.goals.stream().map(PrioritizedGoal::getGoal).collect(Collectors.toList())) {
                        if (goal instanceof MindBendFollowGoal) {
                            return;
                        }
                    }
                    ((MobEntity) entity).goalSelector.addGoal(1, new MindBendFollowGoal((MobEntity) entity, 1.0F, 5.0F, 2.0F));
                    ((MobEntity) entity).targetSelector.addGoal(0, new NullifyAttackableTargetGoal((MobEntity) entity, false));
                    entity.getPersistentData().putUniqueId("PlayerFollowingUUID", player.getUniqueID());
                } else {
                    removalList.add(entity);
                }
            });
        }
        removalList.forEach(MindBenderSpell.mindBendMap::remove);*/
        if (slow_tick_timer < 1) {
            slow_tick_timer = 20;
        } else {
            slow_tick_timer--;
        }
    }

    @SubscribeEvent
    public static void clientTickEvent(TickEvent.ClientTickEvent event) {
        List<LivingEntity> removalList = new ArrayList<>(Collections.emptyList());
        if (!mindbend_particle_handlers.isEmpty()) {
            mindbend_particle_handlers.keySet().removeIf(entity -> entity == null || entity.removed);
            mindbend_particle_handlers.forEach((entity, timer) -> {
                if (!entity.getPersistentData().getBoolean("HexParticle") || entity.removed) {
                    removalList.add(entity);
                } else {
                    if (timer <= 0) {
                        entity.world.addParticle(ParticleInit.hex, entity.getPosX(), entity.getPosY() + entity.getHeight() + 0.5F, entity.getPosZ(), entity.getEntityId(), 0.0D, 0.0D);
                        mindbend_particle_handlers.replace(entity, 7);
                    } else {
                        mindbend_particle_handlers.replace(entity, timer - 1);
                    }
                }
            });
        }
        removalList.forEach(mindbend_particle_handlers::remove);
    }

    @SubscribeEvent
    public static void livingFallEvent(LivingFallEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof PlayerEntity) {
            EffectInstance effectinstance1 = entity.getActivePotionEffect(EffectInit.multi_jump);
            EffectInstance effectinstance2 = entity.getActivePotionEffect(Effects.JUMP_BOOST);
            float f1 = effectinstance1 == null ? 0.0F : (float)(effectinstance1.getAmplifier() + 1);
            float f2 = effectinstance2 == null ? 0.0F : (float)(effectinstance2.getAmplifier() + 1);
            int fallMod = (int) (event.getDistance() - f2 - 3.0F);
            float oldMultiplier = event.getDamageMultiplier();
            int oldFallDamage = MathHelper.ceil(fallMod * oldMultiplier);
            int newFallDamage = MathHelper.ceil((fallMod - f1) * oldMultiplier);
            float multiplier = (float) newFallDamage / oldFallDamage;
            event.setDamageMultiplier(oldMultiplier * multiplier);
            if (entity.isPotionActive(EffectInit.light_footed)) {
                Random rand = new Random();
                World world = entity.world;
                event.setDamageMultiplier(0);
                if (world.isRemote() && newFallDamage > 0)
                for (int i = 0; i < 10; i++) {
                    world.addParticle(ParticleTypes.POOF, entity.getPosX(), entity.getPosY() + 0.05, entity.getPosZ(), (rand.nextFloat() - rand.nextFloat()) * 0.15, (rand.nextFloat() - rand.nextFloat()) * 0.05, (rand.nextFloat() - rand.nextFloat()) * 0.15);
                }
            }
            if (entity.isPotionActive(EffectInit.multi_jump)) {
                MultiJumpEffect.unjumpEntity((PlayerEntity) entity);
            }
        }
    }

    @SubscribeEvent
    public static void playerFallEvent(PlayerFlyableFallEvent event) {
        if (event.getPlayer().isPotionActive(EffectInit.multi_jump)) {
            MultiJumpEffect.unjumpEntity(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void expEvent(PlayerXpEvent.XpChange event) {
        if (TomesConfig.dying_knowledge_enabled.get() && SpellEnchantUtil.hasEnchant(event.getPlayer(), EnchantmentInit.dying_knowledge)) {
            event.setAmount((int) ((float) event.getAmount() * 1.5F));
        }
    }

}
