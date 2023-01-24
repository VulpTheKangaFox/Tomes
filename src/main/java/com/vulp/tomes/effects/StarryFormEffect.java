package com.vulp.tomes.effects;

import com.vulp.tomes.capabilities.IStarryFormReturn;
import com.vulp.tomes.capabilities.StarryFormReturnHolder;
import com.vulp.tomes.capabilities.StarryFormReturnProvider;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerStarryFormMessage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.*;

public class StarryFormEffect extends TomeEffect {

    private static Set<LivingEntity> TRACKER = Collections.newSetFromMap(new WeakHashMap<>());
    public StarryFormEffect() {
        super(EffectType.NEUTRAL, 5840012, true);
    }

    @Override
    public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        if (entityLivingBaseIn instanceof PlayerEntity) {
            Optional<IStarryFormReturn> returnPoint = entityLivingBaseIn.getCapability(StarryFormReturnProvider.CAPABILITY, null).resolve();
            returnPoint.ifPresent(iStarryFormReturn -> iStarryFormReturn.setHolder(new StarryFormReturnHolder(entityLivingBaseIn.getPosition(), entityLivingBaseIn.getEntityWorld().getDimensionKey())));
        }
        TRACKER.add(entityLivingBaseIn);
        TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerStarryFormMessage(TRACKER.toArray(new LivingEntity[]{})));
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    @Override
    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        if (entityLivingBaseIn instanceof PlayerEntity) {
            ((PlayerEntity) entityLivingBaseIn).abilities.isFlying = ((PlayerEntity) entityLivingBaseIn).isCreative();
            ((PlayerEntity) entityLivingBaseIn).sendPlayerAbilities();
            float f1 = entityLivingBaseIn.getSize(entityLivingBaseIn.getPose()).width * 0.8F;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.withSizeAtOrigin(f1, 0.1F, f1).offset(entityLivingBaseIn.getPosX(), entityLivingBaseIn.getPosYEye(), entityLivingBaseIn.getPosZ());
            World world = entityLivingBaseIn.getEntityWorld();
            if (entityLivingBaseIn.getEntityWorld().func_241457_a_(entityLivingBaseIn, axisalignedbb, (state, pos) -> state.isSuffocating(world, pos)).findAny().isPresent()) { // Resets the player if stuck in block.
                Optional<IStarryFormReturn> returnPoint = entityLivingBaseIn.getCapability(StarryFormReturnProvider.CAPABILITY, null).resolve();
                if (returnPoint.isPresent() && returnPoint.get().hasHolder()) {
                    StarryFormReturnHolder data = returnPoint.get().getHolder();
                    if (data != null) {
                        ServerWorld resetDim = ((ServerWorld)world).getServer().getWorld(data.getStartDim());
                        BlockPos resetPos = data.getStartPos();
                        if (resetDim != null && resetPos != null) {
                            if (data.getStartDim() != world.getDimensionKey()) {
                                entityLivingBaseIn.changeDimension(resetDim); // Just in case the player changes dimension and gets lost in a block before changing back. Simple precaution.
                            }
                            entityLivingBaseIn.setPositionAndUpdate(resetPos.getX(), resetPos.getY(), resetPos.getZ());
                        }

                    }
                }
            }
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
