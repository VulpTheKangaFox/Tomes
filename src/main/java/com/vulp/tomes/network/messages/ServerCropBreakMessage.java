package com.vulp.tomes.network.messages;

import com.vulp.tomes.init.ParticleInit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class ServerCropBreakMessage implements IMessage<ServerCropBreakMessage> {

    private double x;
    private double y;
    private double z;

    public ServerCropBreakMessage() {
    }

    public ServerCropBreakMessage(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void encode(ServerCropBreakMessage message, PacketBuffer buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
    }

    @Override
    public ServerCropBreakMessage decode(PacketBuffer buffer) {
        return new ServerCropBreakMessage(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerCropBreakMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                World world = player.getEntityWorld();
                Random rand = new Random();
                for (int i = 0; i < 5; i++) {
                    world.addParticle(ParticleInit.living_wisp, message.x + rand.nextFloat(), message.y + rand.nextFloat(), message.z + rand.nextFloat(), 0.0F, 0.0F, 0.0F);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}