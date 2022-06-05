package com.vulp.tomes.effects;

import com.vulp.tomes.init.EffectInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerStarryFormMessage;
import com.vulp.tomes.world.data.StarryFormSavedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.*;

public class StarryFormEffect extends TomeEffect {

    public static UUID[] UUID_CACHE; // A cache of entity ids created on world load to be organized into proper entities then nullified as soon as a world object is obtainable.
    private static final Set<LivingEntity> TRACKER = Collections.newSetFromMap(new WeakHashMap<>());

    public StarryFormEffect() {
        super(EffectType.NEUTRAL, 5840012, true);
    }

    @Override
    public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        TRACKER.add(entityLivingBaseIn);
        TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerStarryFormMessage(TRACKER.toArray(new LivingEntity[]{})));
        markDirty(entityLivingBaseIn);
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    @Override
    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        if (entityLivingBaseIn instanceof PlayerEntity) {
            ((PlayerEntity) entityLivingBaseIn).abilities.isFlying = ((PlayerEntity) entityLivingBaseIn).isCreative();
            ((PlayerEntity) entityLivingBaseIn).abilities.disableDamage = ((PlayerEntity) entityLivingBaseIn).isCreative() || entityLivingBaseIn.isSpectator();
            ((PlayerEntity) entityLivingBaseIn).sendPlayerAbilities();
        }
        TRACKER.remove(entityLivingBaseIn);
        TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerStarryFormMessage(TRACKER.toArray(new LivingEntity[]{})));
        markDirty(entityLivingBaseIn);
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    // NOTE: Last resort might be to have to have each entity tick if they're not on the list, and when ticked add them to the list. Y'know, instead of all this complex stuff that isn't working.
    @Override
    void potionTick(LivingEntity entityLivingBaseIn, int amplifier) {
        World world = entityLivingBaseIn.getEntityWorld();
        if (!world.isRemote()) {
            boolean flag = false;
            for (UUID i : UUID_CACHE) {
                Entity entity = ((ServerWorld)world).getEntityByUuid(i);
                if (entity instanceof LivingEntity && ((LivingEntity) entity).isPotionActive(EffectInit.starry_form)) {
                    TRACKER.add((LivingEntity) entity);
                    flag = true;
                }
            }
            if (flag) {
                TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerStarryFormMessage(TRACKER.toArray(new LivingEntity[]{})));
                markDirty((ServerWorld) world);
                UUID_CACHE = null;
            }
        }
    }

    public static void markDirty(@Nonnull LivingEntity entity) {
        World world = entity.getEntityWorld();
        if (!world.isRemote()) {
            StarryFormSavedData.grabData((ServerWorld) world).markDirty();
        }
    }

    public static void markDirty(@Nonnull ServerWorld world) {
        StarryFormSavedData.grabData(world).markDirty();
    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return UUID_CACHE != null && UUID_CACHE.length > 0;
    }

    public static boolean hasEntity(LivingEntity entity) {
        return TRACKER.contains(entity);
    }

    public static Set<LivingEntity> getTracker() {
        return TRACKER;
    }

    public static void updateTracker(LivingEntity[] entity) {
        TRACKER.clear();
        TRACKER.addAll(Arrays.asList(entity));
    }

}
