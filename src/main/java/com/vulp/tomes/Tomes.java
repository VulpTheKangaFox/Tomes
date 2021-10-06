package com.vulp.tomes;

import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.init.ParticleInit;
import com.vulp.tomes.init.TagInit;
import com.vulp.tomes.network.TomesPacketHandler;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("tomes")
public class Tomes {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "tomes";

    public Tomes() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::particleSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TomesConfig.SPEC, "tomes-common.toml");

        MinecraftForge.EVENT_BUS.register(this);

        TagInit.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        TomesPacketHandler.init();
        LOGGER.info("Common setup event complete!");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        TomesRegistry.registerRenderers(event);
        LOGGER.info("Client setup event complete!");
    }

    private void particleSetup(final ParticleFactoryRegisterEvent event) {
        ParticleInit.registerFactories();
        LOGGER.info("Particle setup event complete!");
    }

}
