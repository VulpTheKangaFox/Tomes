package com.vulp.tomes.events;

import com.google.common.collect.ImmutableSet;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.init.EffectInit;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.inventory.container.WitchMerchantContainer;
import com.vulp.tomes.items.DebugItem;
import com.vulp.tomes.items.TomeItem;
import com.vulp.tomes.util.SpellEnchantUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Random;

@Mod.EventBusSubscriber(modid=Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

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

/*
    @SubscribeEvent
    public static void onLivingHealEvent(LivingHealEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isPotionActive(EffectInit.leaden_veins)) {
            ((PlayerEntity) entity).heal();
        }
    }
*/

    @SubscribeEvent
    public static void onLivingDropsEvent(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        Random rand = new Random();
        if (entity instanceof WitchEntity) {
            if (rand.nextInt(2) == 0) {
                event.getDrops().add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY() + entity.getYOffset(), entity.getPosZ(), new ItemStack(ItemInit.sweet_heart)));
            }
        } else if (entity instanceof VillagerEntity || entity instanceof AbstractIllagerEntity) {
            if (rand.nextInt(6) == 0) {
                event.getDrops().add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY() + entity.getYOffset(), entity.getPosZ(), new ItemStack(ItemInit.beating_heart)));
            }
        } else if (entity instanceof ZombieEntity) {
            if (rand.nextInt(20) == 0) {
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
                // TODO: Set the data handling to work on one side, so that the randomization doesn't desync???
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

    // NOTE: this will double block drops from crops, not add a percentile chance of an extra drop. The latter is too tricky to pull off.
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        if (SpellEnchantUtil.hasEnchant(player, EnchantmentInit.advantageous_growth) && state.getBlock() instanceof CropsBlock) {
            Block.spawnDrops(state, world, pos);
        }
    }

    @SubscribeEvent
    public static void projHitEvent(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        Vector3d vector3d1 = entity.getPositionVec();
        Vector3d vector3d2 = vector3d1.add(entity.getMotion());
        RayTraceResult result = event.getRayTraceResult();
        EntityRayTraceResult raytrace = ProjectileHelper.rayTraceEntities(event.getEntity().world, entity, vector3d1, vector3d2, entity.getBoundingBox().expand(entity.getMotion()).grow(1.0D), i -> true);
        if (result != null && raytrace != null && result.getType() == RayTraceResult.Type.ENTITY) {
            Entity victim = raytrace.getEntity();
            Entity source = event.getEntity();
            if (victim instanceof PlayerEntity && SpellEnchantUtil.hasEnchant((PlayerEntity) victim, EnchantmentInit.airy_protection) && source instanceof ProjectileEntity && new Random().nextBoolean()) {
                Vector3d vec = source.getMotion().inverse();
                source.setVelocity(vec.getX() * 0.75, vec.getY() * 0.75, vec.getZ() * 0.75);
                source.rotationPitch = MathHelper.wrapDegrees(source.rotationPitch + 180);
                source.rotationYaw = MathHelper.wrapDegrees(source.rotationYaw + 180);
                if (source instanceof DamagingProjectileEntity) {
                    DamagingProjectileEntity damageProj = (DamagingProjectileEntity) source;
                    damageProj.accelerationX = -damageProj.accelerationX * 0.75;
                    damageProj.accelerationY = -damageProj.accelerationY * 0.75;
                    damageProj.accelerationZ = -damageProj.accelerationZ * 0.75;
                }
                ((ProjectileEntity) source).setShooter(victim);
                source.velocityChanged = true;
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void livingFallEvent(LivingFallEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isPotionActive(EffectInit.light_footed)) {
            event.setDamageMultiplier(0);
        }
    }



    @SubscribeEvent
    public static void expEvent(PlayerXpEvent.XpChange event) {
        if (SpellEnchantUtil.hasEnchant(event.getPlayer(), EnchantmentInit.dying_knowledge)) {
            event.setAmount((int) ((float) event.getAmount() * 1.4F));
        }
    }

}
