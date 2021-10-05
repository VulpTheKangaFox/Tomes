package com.vulp.tomes.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class ServerTameAnimalMessage implements IMessage<ServerTameAnimalMessage> {

    private double[] array;

    public ServerTameAnimalMessage() {
    }

    public ServerTameAnimalMessage(double posX, double posY, double posZ, double width, double height) {
        this.array = new double[]{posX, posY, posZ, width, height};
    }

    @Override
    public void encode(ServerTameAnimalMessage message, PacketBuffer buffer) {
        buffer.writeDouble(message.array[0]);
        buffer.writeDouble(message.array[1]);
        buffer.writeDouble(message.array[2]);
        buffer.writeDouble(message.array[3]);
        buffer.writeDouble(message.array[4]);
    }

    @Override
    public ServerTameAnimalMessage decode(PacketBuffer buffer) {
        return new ServerTameAnimalMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void handle(ServerTameAnimalMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                World world = Minecraft.getInstance().player.getEntityWorld();
                Random rand = new Random();
                double[] array = message.array;
                for(int i = 0; i < 7; ++i) {
                    double d0 = rand.nextGaussian() * 0.02D;
                    double d1 = rand.nextGaussian() * 0.02D;
                    double d2 = rand.nextGaussian() * 0.02D;
                    world.addParticle(ParticleTypes.HEART, array[0] + array[3] * (2.0D * rand.nextDouble() - 1.0D), (array[1] + array[4] * rand.nextDouble()) + 0.5D, array[2] + array[3] * (2.0D * rand.nextDouble() - 1.0D), d0, d1, d2);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}