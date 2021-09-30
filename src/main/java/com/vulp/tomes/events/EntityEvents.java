package com.vulp.tomes.events;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.init.EffectInit;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.inventory.container.WitchMerchantContainer;
import com.vulp.tomes.items.DebugItem;
import com.vulp.tomes.items.TomeItem;
import com.vulp.tomes.util.SpellEnchantUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.OptionalInt;
import java.util.Random;

@Mod.EventBusSubscriber(modid=Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

    @SubscribeEvent
    public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
        Entity player = event.getTarget();
        Entity witch = event.getEntity();
        if (event.getTarget() instanceof PlayerEntity && event.getEntity() instanceof WitchEntity) {
            if (SpellEnchantUtil.hasEnchant((PlayerEntity) player, EnchantmentInit.covens_rule)) {
                ((WitchEntity)witch).setAttackTarget(null);
            }
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
                // TODO: Create MerchantOffers and an Inventory and store them into the witch. Refer to their code instead of variables since we can't use those, maybe make some methods to handle most of the work?
                if (!world.isRemote) {
                    CompoundNBT nbt = entity.getPersistentData();
                    CompoundNBT nbtOffers = nbt.getCompound("Offers");
                    boolean flag = true;
                    if (nbt.hasUniqueId("Customer")) {
                        if (nbt.getUniqueId("Customer") != player.getUniqueID()) {
                            flag = false;
                        }
                    } else {
                        nbt.putUniqueId("Customer", player.getUniqueID());
                    }
                    if (flag) {
                        final WitchMerchantContainer[] container = new WitchMerchantContainer[1];
                        OptionalInt optionalint = player.openContainer(new SimpleNamedContainerProvider((id, playerInventory, player2) -> {
                            container[0] = new WitchMerchantContainer(id, playerInventory, (WitchEntity) entity);
                            return container[0];
                        }, entity.getDisplayName()));
                        if (optionalint.isPresent()) {
                            container[0].getOffers();
                            MerchantOffers offers = new MerchantOffers(nbtOffers);
                            if (!offers.isEmpty()) {
                                player.openMerchantContainer(optionalint.getAsInt(), offers, 0, 0, false, false);
                            }
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
