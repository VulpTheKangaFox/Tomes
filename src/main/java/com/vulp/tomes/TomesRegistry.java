package com.vulp.tomes;

import com.vulp.tomes.client.renderer.entity.renderers.SpectralSteedRenderer;
import com.vulp.tomes.client.renderer.entity.renderers.TamedSpiderRenderer;
import com.vulp.tomes.client.renderer.entity.renderers.WildWolfRenderer;
import com.vulp.tomes.client.renderer.entity.renderers.WitheringStenchRenderer;
import com.vulp.tomes.effects.LeadenVeinsEffect;
import com.vulp.tomes.effects.LightFootedEffect;
import com.vulp.tomes.effects.MindBendEffect;
import com.vulp.tomes.init.*;
import com.vulp.tomes.items.DebugItem;
import com.vulp.tomes.items.HiddenDescriptorItem;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class TomesRegistry {

    public static final ItemGroup TOMES_TAB = new ItemGroup("tomes") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemInit.archaic_tome);
        }
    };

    @SubscribeEvent
    public static void itemRegistryEvent(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                ItemInit.archaic_tome = new TomeItem(new Item.Properties().group(TOMES_TAB).maxDamage(1000)).setRegistryName(location("archaic_tome")),
                ItemInit.living_tome = new TomeItem(new Item.Properties().group(TOMES_TAB).maxDamage(1000)).setRegistryName(location("living_tome")),
                ItemInit.cursed_tome = new TomeItem(new Item.Properties().group(TOMES_TAB).maxDamage(1000)).setRegistryName(location("cursed_tome")),

                ItemInit.ancient_heart = new HiddenDescriptorItem(new Item.Properties().group(TOMES_TAB)).setRegistryName(location("ancient_heart")),
                ItemInit.beating_heart = new HiddenDescriptorItem(new Item.Properties().group(TOMES_TAB)).setRegistryName(location("beating_heart")),
                ItemInit.sweet_heart = new HiddenDescriptorItem(new Item.Properties().group(TOMES_TAB)).setRegistryName(location("sweet_heart")),

                ItemInit.archaic_tome_open = new Item(new Item.Properties()).setRegistryName(location("archaic_tome_open")),
                ItemInit.living_tome_open = new Item(new Item.Properties()).setRegistryName(location("living_tome_open")),
                ItemInit.cursed_tome_open = new Item(new Item.Properties()).setRegistryName(location("cursed_tome_open")),

                ItemInit.debug_tome = new DebugItem(new Item.Properties()).setRegistryName(location("debug_tome"))
        );

        Tomes.LOGGER.info("Items Registered!");
    }

    @SubscribeEvent
    public static void enchantRegistryEvent(final RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().registerAll(
                EnchantmentInit.self_propulsion.setRegistryName(location("self_propulsion")),
                EnchantmentInit.dying_knowledge.setRegistryName(location("dying_knowledge")),
                EnchantmentInit.linguist.setRegistryName(location("linguist")),
                EnchantmentInit.strike_from_above.setRegistryName(location("strike_from_above")),
                EnchantmentInit.airy_protection.setRegistryName(location("airy_protection")),
                EnchantmentInit.force_of_wind.setRegistryName(location("force_of_wind")),
                EnchantmentInit.everchanging_skies.setRegistryName(location("everchanging_skies")),

                EnchantmentInit.lifebringer.setRegistryName(location("lifebringer")),
                EnchantmentInit.beast_tamer.setRegistryName(location("beast_tamer")),
                EnchantmentInit.wild_aid.setRegistryName(location("wild_aid")),
                EnchantmentInit.nurturing_roots.setRegistryName(location("nurturing_roots")),
                EnchantmentInit.advantageous_growth.setRegistryName(location("advantageous_growth")),
                EnchantmentInit.forest_affinity.setRegistryName(location("forest_affinity")),
                EnchantmentInit.molding_lands.setRegistryName(location("molding_lands")),

                EnchantmentInit.mind_bender.setRegistryName(location("mind_bender")),
                EnchantmentInit.ghostly_steed.setRegistryName(location("ghostly_steed")),
                EnchantmentInit.withering_stench.setRegistryName(location("withering_stench")),
                EnchantmentInit.covens_rule.setRegistryName(location("covens_rule")),
                EnchantmentInit.rotten_heart.setRegistryName(location("rotten_heart")),
                EnchantmentInit.nocturnal.setRegistryName(location("nocturnal")),
                EnchantmentInit.dark_age.setRegistryName(location("dark_age"))
        );

        Tomes.LOGGER.info("Enchantments Registered!");
    }

    @SubscribeEvent
    public static void containerRegistryEvent(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                ContainerInit.witch_merchant_container.setRegistryName(Tomes.MODID, "witch_merchant_container")
        );
        Tomes.LOGGER.info("Containers Registered!");
    }

    @SubscribeEvent
    public static void entityRegistryEvent(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                EntityInit.wild_wolf,
                EntityInit.tamed_spider,
                EntityInit.spectral_steed,
                EntityInit.withering_stench
                );

        EntityInit.setupAttributes();

        Tomes.LOGGER.info("Entities Registered!");
    }

    @SubscribeEvent
    public static void effectRegistryEvent(final RegistryEvent.Register<Effect> event) {
        event.getRegistry().registerAll(
                EffectInit.light_footed.setRegistryName(location("light_footed")),
                EffectInit.mind_bend.setRegistryName(location("mind_bend")),
                EffectInit.leaden_veins.setRegistryName(location("leaden_veins"))
        );

        Tomes.LOGGER.info("Enchantments Registered!");
    }

    @SubscribeEvent
    public static void onParticleRegistry(final RegistryEvent.Register<ParticleType<?>> event)
    {
        event.getRegistry().registerAll
                (
                        ParticleInit.spirit_flame.setRegistryName(location("spirit_flame")),
                        ParticleInit.withering_stench.setRegistryName(location("withering_stench"))
                );

        Tomes.LOGGER.info("Particles Registered!");
    }


    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.wild_wolf, new WildWolfRenderer.RenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.tamed_spider, new TamedSpiderRenderer.RenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.spectral_steed, new SpectralSteedRenderer.RenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.withering_stench, new WitheringStenchRenderer.RenderFactory());
    }

    public static ResourceLocation location(String name) {
        return new ResourceLocation(Tomes.MODID, name);
    }

}
