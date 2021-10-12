package com.vulp.tomes.network;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.network.messages.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class TomesPacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    private static int nextId = 0;

    public static SimpleChannel instance;

    public static void init() {
        instance = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Tomes.MODID, "network"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();
        register(ServerActiveSpellMessage.class, new ServerActiveSpellMessage());
        register(ServerTameAnimalMessage.class, new ServerTameAnimalMessage());
        register(ServerEnchantmentClueMessage.class, new ServerEnchantmentClueMessage());
        register(ServerWitheringStenchParticleMessage.class, new ServerWitheringStenchParticleMessage());
        register(ServerOpenHorseInventoryMessage.class, new ServerOpenHorseInventoryMessage());
        register(ServerProjDeflectMessage.class, new ServerProjDeflectMessage());
        register(ServerSpiderFallMessage.class, new ServerSpiderFallMessage());
        register(ServerMindBendMessage.class, new ServerMindBendMessage());
        register(ServerSyncPersistentDataMessage.class, new ServerSyncPersistentDataMessage());
        register(ServerSummonDespawnMessage.class, new ServerSummonDespawnMessage());
        register(ServerCropBreakMessage.class, new ServerCropBreakMessage());
    }

    private static <T> void register(Class<T> clazz, IMessage<T> message)
    {
        instance.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
    }

}
