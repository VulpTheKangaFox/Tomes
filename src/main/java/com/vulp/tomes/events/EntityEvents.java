package com.vulp.tomes.events;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.init.*;
import com.vulp.tomes.inventory.container.WitchMerchantContainer;
import com.vulp.tomes.items.DebugItem;
import com.vulp.tomes.items.TomeItem;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerCropBreakMessage;
import com.vulp.tomes.network.messages.ServerMindBendMessage;
import com.vulp.tomes.network.messages.ServerProjDeflectMessage;
import com.vulp.tomes.util.SpellEnchantUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid=Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

    private static int mindbend_particle_timer = 7;

    @SubscribeEvent
    public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
        Entity player = event.getTarget();
        Entity aggressor = event.getEntity();
        if (event.getTarget() instanceof PlayerEntity) {
            if (event.getEntity() instanceof WitchEntity) {
                if (SpellEnchantUtil.hasEnchant((PlayerEntity) player, EnchantmentInit.covens_rule)) {
                    ((WitchEntity) aggressor).setAttackTarget(null);
                }
            }
            if (aggressor instanceof SpiderEntity || aggressor instanceof CreeperEntity || aggressor instanceof ZombieEntity || aggressor instanceof SkeletonEntity) {
                if (SpellEnchantUtil.hasEnchant((PlayerEntity) player, EnchantmentInit.rotten_heart)) {
                    ((MobEntity)aggressor).setAttackTarget(null);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDropsEvent(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        Random rand = new Random();
        if (entity instanceof WitchEntity) {
            if (rand.nextInt(19) < TomesConfig.sweet_heart_droprate.get()) {
                event.getDrops().add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY() + entity.getYOffset(), entity.getPosZ(), new ItemStack(ItemInit.sweet_heart)));
            }
        } else if (entity instanceof VillagerEntity || entity instanceof AbstractIllagerEntity) {
            if (rand.nextInt(19) < TomesConfig.beating_heart_droprate.get()) {
                event.getDrops().add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY() + entity.getYOffset(), entity.getPosZ(), new ItemStack(ItemInit.beating_heart)));
            }
        } else if (entity instanceof ZombieEntity) {
            if (rand.nextInt(19) < TomesConfig.archaic_heart_droprate.get()) {
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
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        PlayerEntity player = event.getPlayer();
        Entity entity = event.getTarget();
        World world = player.world;
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        if (item instanceof TomeItem) {
            if (entity instanceof AbstractHorseEntity) {
                if (SpellEnchantUtil.hasEnchant(player, EnchantmentInit.beast_tamer)) {
                    Hand hand = Hand.OFF_HAND;
                    if (stack == player.getHeldItemMainhand()) {
                        hand = Hand.MAIN_HAND;
                    }
                    item.itemInteractionForEntity(stack, player, (LivingEntity) entity, hand);
                    item.onItemRightClick(world, player, hand);
                }
            } else if (entity instanceof WitchEntity) {
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
        if (item instanceof DebugItem && entity instanceof WitchEntity) {
            ((DebugItem)item).debugWitchNBT(world, entity, stack);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        if (SpellEnchantUtil.hasEnchant(player, EnchantmentInit.advantageous_growth) && state.getBlock() instanceof CropsBlock && ((CropsBlock) state.getBlock()).isMaxAge(state) && !player.isCreative()) {
            Block.spawnDrops(state, world, pos);
            TomesPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with((() -> (Chunk) world.getChunk(pos))), new ServerCropBreakMessage(pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    @SubscribeEvent
    public static void projHitEvent(ProjectileImpactEvent event) {
        Entity proj = event.getEntity();
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
                    proj.setMotion(vec2.getX() * 0.75, vec2.getY() * 0.75, vec2.getZ() * 0.75);
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

    @SubscribeEvent
    public static void livingTickEvent(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity != null) {
            if (entity.world.isRemote) {
                if (entity.getPersistentData().getBoolean("HexParticle")) {
                    if (mindbend_particle_timer <= 0) {
                        entity.world.addParticle(ParticleInit.hex, entity.getPosX(), entity.getPosY() + entity.getHeight() + 0.5F, entity.getPosZ(), entity.getEntityId(), 0.0D, 0.0D);
                        mindbend_particle_timer = 7;
                    } else {
                        mindbend_particle_timer--;
                    }
                }
            } else {
                TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerMindBendMessage(entity.getEntityId(), entity.getPersistentData().getBoolean("HexParticle")));
            }
        }
    }

    @SubscribeEvent
    public static void livingFallEvent(LivingFallEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isPotionActive(EffectInit.light_footed)) {
            event.setDamageMultiplier(0);
            Random rand = new Random();
            World world = entity.world;
            if (world.isRemote)
            for (int i = 0; i < 10; i++) {
                world.addParticle(ParticleTypes.POOF, entity.getPosX(), entity.getPosY() + 0.05, entity.getPosZ(), (rand.nextFloat() - rand.nextFloat()) * 0.15, (rand.nextFloat() - rand.nextFloat()) * 0.05, (rand.nextFloat() - rand.nextFloat()) * 0.15);
            }
        }
    }

    @SubscribeEvent
    public static void expEvent(PlayerXpEvent.XpChange event) {
        if (SpellEnchantUtil.hasEnchant(event.getPlayer(), EnchantmentInit.dying_knowledge)) {
            event.setAmount((int) ((float) event.getAmount() * 1.5F));
        }
    }

}
