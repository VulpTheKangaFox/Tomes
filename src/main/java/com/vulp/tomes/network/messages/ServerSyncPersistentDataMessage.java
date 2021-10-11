package com.vulp.tomes.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerSyncPersistentDataMessage implements IMessage<ServerSyncPersistentDataMessage> {

    private CompoundNBT nbt;
    private int entityID;

    public ServerSyncPersistentDataMessage() {
    }

    public ServerSyncPersistentDataMessage(CompoundNBT nbt, int entityID) {
        this.nbt = nbt;
        this.entityID = entityID;
    }

    @Override
    public void encode(ServerSyncPersistentDataMessage message, PacketBuffer buffer) {
        buffer.writeCompoundTag(message.nbt);
        buffer.writeInt(message.entityID);
    }

    @Override
    public ServerSyncPersistentDataMessage decode(PacketBuffer buffer) {
        return new ServerSyncPersistentDataMessage(buffer.readCompoundTag(), buffer.readInt());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerSyncPersistentDataMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                World world = player.getEntityWorld();
                Entity entity = world.getEntityByID(message.entityID);
                if (entity != null) {
                    entity.getPersistentData().merge(message.nbt.copy());
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}