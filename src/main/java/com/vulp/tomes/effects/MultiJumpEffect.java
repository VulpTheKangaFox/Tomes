package com.vulp.tomes.effects;

import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.init.EffectInit;
import com.vulp.tomes.init.ParticleInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.WeakHashMap;

public class MultiJumpEffect extends TomeEffect {

    private static final WeakHashMap<PlayerEntity, Pair<Integer, Integer>> jumpers = new WeakHashMap<>();

    public MultiJumpEffect() {
        super(EffectType.BENEFICIAL, 65489, false);
    }

    @Override
    void potionTick(LivingEntity entityLivingBaseIn, int amplifier) {

    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return false;
    }

    @Override
    public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        EffectInstance effect = entityLivingBaseIn.getActivePotionEffect(this);
        if (entityLivingBaseIn instanceof PlayerEntity && (effect == null || (jumpers.containsKey(entityLivingBaseIn) && jumpers.get(entityLivingBaseIn).getSecond() != amplifier + 1))) {
            addJumper((PlayerEntity) entityLivingBaseIn, amplifier + 1);
        }
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    @Override
    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        EffectInstance effect = entityLivingBaseIn.getActivePotionEffect(this);
        if (entityLivingBaseIn instanceof PlayerEntity && effect == null) {
            removeJumper((PlayerEntity) entityLivingBaseIn);
        }
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    public static void doAirJump(PlayerEntity entity) {
        Pair<Integer, Integer> pair = jumpers.get(entity);
        if (pair == null) {
            EffectInstance effect = entity.getActivePotionEffect(EffectInit.multi_jump);
            if (effect != null) {
                pair = addJumper(entity, effect.getAmplifier() + 1);
            }
        }
        if (pair != null && pair.getFirst() < pair.getSecond()) {
            entity.jump();
            entity.fallDistance = 0.0F;
            World world = entity.world;
            if (world.isRemote()) {
                Vector3d vector3d = entity.getMotion();
                float f = MathHelper.sqrt(vector3d.x * vector3d.x + vector3d.z * vector3d.z);
                world.addParticle(ParticleInit.wind_deflect, (float)entity.getPosX(), (float)entity.getPosY(), (float)entity.getPosZ(), MathHelper.atan2(vector3d.y, f) * (double)(180F / (float)Math.PI), 90 + (MathHelper.atan2(vector3d.z, vector3d.x) * (double)(180F / (float)Math.PI)), 1.6D);
            } else {
                jumpers.put(entity, new Pair<>(pair.getFirst() + 1, pair.getSecond()));
            }
        }
    }

    public static Pair<Integer, Integer> addJumper(PlayerEntity entity, int extraJumps) {
        Pair<Integer, Integer> pair = new Pair<>(0, extraJumps);
        jumpers.put(entity, pair);
        return pair;
    }

    public static void removeJumper(PlayerEntity entity) {
        jumpers.remove(entity);
    }

    public static void unjumpEntity(PlayerEntity entity) {
        Pair<Integer, Integer> pair = jumpers.get(entity);
        if (pair != null) {
            jumpers.put(entity, new Pair<>(0, pair.getSecond()));
        }
    }

}
