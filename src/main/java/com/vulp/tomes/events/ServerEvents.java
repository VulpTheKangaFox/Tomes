package com.vulp.tomes.events;

import com.google.common.collect.Lists;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.effects.StarryFormEffect;
import com.vulp.tomes.init.EffectInit;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerStarryFormMessage;
import com.vulp.tomes.util.SpellEnchantUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid= Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            TomesPacketHandler.instance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getEntity()), new ServerStarryFormMessage(StarryFormEffect.getTracker().toArray(new LivingEntity[]{})));
        }
    }

    // Potentially replace with an 'on entity load' check on the serverside.
    /*@SubscribeEvent
    public static void onLoadWorld(WorldEvent.Load event) {
        IWorld world = event.getWorld();
        if (world instanceof ServerWorld) {
            Set<LivingEntity> tracker = StarryFormEffect.getTracker();
            ((ServerWorld) world).getProfiler().func_230035_c_("getLoadedEntities");
            Int2ObjectMap<ChunkManager.EntityTracker> entities = ((ServerChunkProvider)world.getChunkProvider()).chunkManager.entities;
            entities.forEach((integer, entityTracker) -> {
                Entity entity = entityTracker.entity;
                if (entity instanceof LivingEntity && ((LivingEntity) entity).isPotionActive(EffectInit.starry_form)) {
                    tracker.add((LivingEntity) entity);
                }
            });
            if (tracker.size() > 0) {
                TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerStarryFormMessage(tracker.toArray(new LivingEntity[]{})));
            }
        }
    }*/

}
