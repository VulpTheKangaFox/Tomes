package com.vulp.tomes.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerSummonDespawnMessage implements IMessage<ServerSummonDespawnMessage> {

    private int entityID;
    private int particleID;

    public ServerSummonDespawnMessage() {
    }

    public ServerSummonDespawnMessage(int entityID, int particleID) {
        this.entityID = entityID;
        this.particleID = particleID;
    }

    @Override
    public void encode(ServerSummonDespawnMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.entityID);
        buffer.writeInt(message.particleID);
    }

    @Override
    public ServerSummonDespawnMessage decode(PacketBuffer buffer) {
        return new ServerSummonDespawnMessage(buffer.readInt(), buffer.readInt());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerSummonDespawnMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                World world = player.world;
                Entity entity = world.getEntityByID(message.entityID);
                ParticleType<?> particle = Registry.PARTICLE_TYPE.getByValue(message.particleID);
                if (entity != null && particle != null) {
                    world.addParticle((IParticleData) particle, entity.getPosX(), entity.getPosY(), entity.getPosZ(), message.entityID, 0.0F, 0.0F);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
