package com.vulp.tomes.init;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.entities.*;
import com.vulp.tomes.entities.projectile.DeathlyIchorEntity;
import com.vulp.tomes.entities.projectile.WitheringStenchEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Tomes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityInit {

    public static final EntityType<WildWolfEntity> wild_wolf = createEntity(WildWolfEntity::new, EntityClassification.CREATURE, "wild_wolf", 0.6F, 0.85F, 10);
    public static final EntityType<TamedSpiderEntity> tamed_spider = createEntity(TamedSpiderEntity::new, EntityClassification.CREATURE, "tamed_spider", 1.4F, 0.9F, 8);
    public static final EntityType<SpectralSteedEntity> spectral_steed = createEntity(SpectralSteedEntity::new, EntityClassification.CREATURE, "spectral_steed", 1.3964844F, 1.6F, 10);
    public static final EntityType<WitheringStenchEntity> withering_stench = createEntity(WitheringStenchEntity::new, EntityClassification.MISC, "withering_stench", 0.25F, 0.25F, 10);
    public static final EntityType<DeathlyIchorEntity> deathly_ichor = createEntity(DeathlyIchorEntity::new, EntityClassification.MISC, "deathly_ichor", 0.5F, 0.5F, 10);
    public static final EntityType<WitherBallEntity> wither_ball = createEntity(WitherBallEntity::new, EntityClassification.MISC, "wither_ball", 0.4F, 0.4F, 10);
    public static final EntityType<WitchOfCogencyEntity> witch_of_cogency = createEntity(WitchOfCogencyEntity::new, EntityClassification.MONSTER, "witch_of_cogency", 0.6F, 1.95F, 10);

    private static <T extends Entity> EntityType<T> createEntity(EntityType.IFactory<T> factory, EntityClassification entityClassification, String name, float width, float height, int trackingRange) {
        ResourceLocation location = new ResourceLocation(Tomes.MODID, name);
        EntityType.Builder<T> builder = EntityType.Builder.create(factory, entityClassification)
                .size(width, height)
                .setShouldReceiveVelocityUpdates(true)
                .setUpdateInterval(3);

        if (trackingRange != -1) {
            builder.setTrackingRange(trackingRange);
        }
        EntityType<T> entity = builder.build(location.toString());
        entity.setRegistryName(location);

        return entity;
    }

    @SubscribeEvent
    public static void setupAttributes(EntityAttributeCreationEvent event) {
        event.put(wild_wolf, WildWolfEntity.registerAttributes().create());
        event.put(tamed_spider, TamedSpiderEntity.bakeAttributes().create());
        event.put(spectral_steed, SpectralSteedEntity.func_234237_fg_().create());
        event.put(witch_of_cogency, WitchOfCogencyEntity.registerAttributes().create());
    }

}
