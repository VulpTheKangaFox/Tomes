package com.vulp.tomes.network.messages;

import com.vulp.tomes.init.ParticleInit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class ServerSpiderFallMessage implements IMessage<ServerSpiderFallMessage> {

    private double x;
    private double y;
    private double z;

    public ServerSpiderFallMessage() {
    }

    public ServerSpiderFallMessage(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void encode(ServerSpiderFallMessage message, PacketBuffer buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
    }

    @Override
    public ServerSpiderFallMessage decode(PacketBuffer buffer) {
        return new ServerSpiderFallMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerSpiderFallMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                World world = player.getEntityWorld();
                Random rand = new Random();
                world.addParticle(ParticleInit.web_net, message.x, message.y + (rand.nextFloat() * 0.10F) + 0.03, message.z, 0.0F, 0.0F, 1.4F);
                for (int i = 0; i < 10; i++) {
                    world.addParticle(ParticleTypes.POOF, message.x, message.y + (rand.nextFloat() * 0.10F) + 0.03, message.z, (rand.nextFloat() - rand.nextFloat()) * 0.25, (rand.nextFloat() - rand.nextFloat()) * 0.04, (rand.nextFloat() - rand.nextFloat()) * 0.25);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}