package com.vulp.tomes.network.messages;

import com.vulp.tomes.effects.StarryFormEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerStarryFormMessage implements IMessage<ServerStarryFormMessage> {

    private LivingEntity[] entities;

    public ServerStarryFormMessage() {
    }

    public ServerStarryFormMessage(LivingEntity[] entities) {
        this.entities = entities;
    }

    @Override
    public void encode(ServerStarryFormMessage message, PacketBuffer buffer) {
        int[] entityIDs = new int[message.entities.length];
        for (int i = 0; i < message.entities.length; i++) {
            if (message.entities[i] != null) {
                entityIDs[i] = message.entities[i].getEntityId();
            }
        }
        buffer.writeVarIntArray(entityIDs);
    }

    @Override
    public ServerStarryFormMessage decode(PacketBuffer buffer) {
        if (Minecraft.getInstance().world != null) {
            int[] entityIDs = buffer.readVarIntArray();
            LivingEntity[] entities = new LivingEntity[entityIDs.length];
            for (int i = 0; i < entityIDs.length; i++) {
                entities[i] = (LivingEntity) Minecraft.getInstance().world.getEntityByID(entityIDs[i]);
            }
            return new ServerStarryFormMessage(entities);
        } else {
            return new ServerStarryFormMessage(null);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerStarryFormMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> StarryFormEffect.updateTracker(message.entities));
        supplier.get().setPacketHandled(true);
    }

}

