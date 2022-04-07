package com.vulp.tomes.spells.active;

import com.google.common.collect.ImmutableSet;
import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.entities.TamedSpiderEntity;
import com.vulp.tomes.init.EntityInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerTameAnimalMessage;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.network.PacketDistributor;

public class BeastTamerSpell extends ActiveSpell {

    private static final ImmutableSet<Class<? extends LivingEntity>> tameableMobs = ImmutableSet.of(WolfEntity.class, CatEntity.class, ParrotEntity.class, FoxEntity.class, HorseEntity.class, SkeletonHorseEntity.class, ZombieHorseEntity.class, DonkeyEntity.class, LlamaEntity.class, MuleEntity.class, SpiderEntity.class);

    public BeastTamerSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        super(rarity, isActive, isRare, enabled);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.beast_tamer_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        LivingEntity target = (LivingEntity) this.getTarget();
        boolean flag = false;
        if (target != null) {
            for (Class<? extends LivingEntity> mob : tameableMobs) {
                if (mob == target.getClass()) {
                    flag = true;
                    if (target instanceof TameableEntity && !((TameableEntity) target).isTamed()) {
                        ((TameableEntity) target).setTamedBy(playerIn);
                    } else if (target instanceof FoxEntity) {
                        ((FoxEntity) target).addTrustedUUID(playerIn.getUniqueID());
                        if (worldIn.isRemote) {
                            tameEffect(target);
                        }
                    } else if (target instanceof AbstractHorseEntity && !((AbstractHorseEntity) target).isTame()) {
                        ((AbstractHorseEntity) target).setTamedBy(playerIn);
                    } else if (target instanceof SpiderEntity) {
                        if (!worldIn.isRemote) {
                            CompoundNBT nbt = target.serializeNBT();
                            float pitch = target.rotationPitch;
                            float yaw = target.rotationYaw;
                            float yawHead = target.rotationYawHead;
                            ITextComponent name = target.getCustomName();
                            TamedSpiderEntity tamedEntity = EntityInit.tamed_spider.spawn((ServerWorld) worldIn, nbt, name, playerIn, target.getPosition(), SpawnReason.CONVERSION, false, false);
                            if (tamedEntity != null) {
                                target.remove();
                                tamedEntity.rotationPitch = pitch;
                                tamedEntity.rotationYaw = yaw;
                                tamedEntity.rotationYawHead = yawHead;
                                tamedEntity.setTamedBy(playerIn);
                            }
                        }
                    } else flag = false;
                }
            }
        }
        this.setTarget(null);
        return flag;
    }

    @OnlyIn(Dist.CLIENT)
    private static void tameEffect(Entity entity) {
        Vector3d vec = entity.getPositionVec();
        TomesPacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new ServerTameAnimalMessage(vec.x, vec.y, vec.z, entity.getWidth(), entity.getHeight()));
    }

    @Override
    public int getCooldown() {
        return TomesConfig.beast_tamer_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity playerIn) {

    }
}