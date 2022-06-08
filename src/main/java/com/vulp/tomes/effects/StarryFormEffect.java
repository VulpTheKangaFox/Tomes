package com.vulp.tomes.effects;

import com.vulp.tomes.init.ParticleInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerStarryFormMessage;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectType;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.*;

public class StarryFormEffect extends TomeEffect {

    private static Set<LivingEntity> TRACKER = Collections.newSetFromMap(new WeakHashMap<>());
    public StarryFormEffect() {
        super(EffectType.NEUTRAL, 5840012, true);
    }

    @Override
    public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        TRACKER.add(entityLivingBaseIn);
        TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerStarryFormMessage(TRACKER.toArray(new LivingEntity[]{})));
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
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    @Override
    void potionTick(LivingEntity entityLivingBaseIn, int amplifier) {
        World world = entityLivingBaseIn.getEntityWorld();
        if (!world.isRemote()) {
            if (!TRACKER.contains(entityLivingBaseIn)) {
                PlayerEntity player = world.getClosestPlayer(entityLivingBaseIn, 100.0D);
                if (player != null) {
                    TRACKER.add(entityLivingBaseIn);
                    TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerStarryFormMessage(TRACKER.toArray(new LivingEntity[]{})));
                }
            }
        }
    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return true;
    }

    public static boolean hasEntity(LivingEntity entity) {
        return TRACKER.contains(entity);
    }

    public static Set<LivingEntity> getTracker() {
        return TRACKER;
    }

    public static void updateTracker(LivingEntity[] entity) {
        TRACKER = new HashSet<>(Arrays.asList(entity));
    }

}
