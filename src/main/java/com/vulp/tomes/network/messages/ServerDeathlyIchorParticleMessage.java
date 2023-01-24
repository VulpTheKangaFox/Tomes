package com.vulp.tomes.network.messages;

import com.vulp.tomes.init.ParticleInit;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerDeathlyIchorParticleMessage implements IMessage<ServerDeathlyIchorParticleMessage> {

    private double posX;
    private double posY;
    private double posZ;
    private int entityID;

    public ServerDeathlyIchorParticleMessage() {
    }

    public ServerDeathlyIchorParticleMessage(double posX, double posY, double posZ, int entityID) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.entityID = entityID;
    }

    @Override
    public void encode(ServerDeathlyIchorParticleMessage message, PacketBuffer buffer) {
        buffer.writeDouble(message.posX);
        buffer.writeDouble(message.posY);
        buffer.writeDouble(message.posZ);
        buffer.writeInt(message.entityID);
    }

    @Override
    public ServerDeathlyIchorParticleMessage decode(PacketBuffer buffer) {
        return new ServerDeathlyIchorParticleMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readInt());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerDeathlyIchorParticleMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            World world = Minecraft.getInstance().world;
            if (world != null) {
                world.addParticle(ParticleInit.deathly_ichor, message.posX, message.posY, message.posZ, (float)message.entityID, 0.0F, 0.0F);
            }
        });
        supplier.get().setPacketHandled(true);
    }

}