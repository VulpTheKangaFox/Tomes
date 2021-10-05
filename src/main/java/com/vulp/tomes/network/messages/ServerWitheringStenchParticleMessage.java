package com.vulp.tomes.network.messages;

import com.vulp.tomes.init.ParticleInit;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class ServerWitheringStenchParticleMessage implements IMessage<ServerWitheringStenchParticleMessage> {

    private double posX;
    private double posY;
    private double posZ;
    private double velX;
    private double velY;
    private double velZ;

    public ServerWitheringStenchParticleMessage() {
    }

    public ServerWitheringStenchParticleMessage(double posX, double posY, double posZ, double velX, double velY, double velZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
    }

    @Override
    public void encode(ServerWitheringStenchParticleMessage message, PacketBuffer buffer) {
        buffer.writeDouble(message.posX);
        buffer.writeDouble(message.posY);
        buffer.writeDouble(message.posZ);
        buffer.writeDouble(message.velX);
        buffer.writeDouble(message.velY);
        buffer.writeDouble(message.velZ);
    }

    @Override
    public ServerWitheringStenchParticleMessage decode(PacketBuffer buffer) {
        return new ServerWitheringStenchParticleMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void handle(ServerWitheringStenchParticleMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            World world = Minecraft.getInstance().world;
            if (world != null) {
                Random rand = new Random();
                for (int i = 0; i < 2; ++i) {
                    double d0 = 0.2D + 0.1D * (double) i;
                    world.addParticle(ParticleInit.withering_stench, message.posX + (rand.nextFloat() * 0.2) - 0.1, message.posY + (rand.nextFloat() * 0.1) - 0.05, message.posZ + (rand.nextFloat() * 0.2) - 0.1, message.velX * d0, message.velY, message.velZ * d0);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}