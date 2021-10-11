package com.vulp.tomes.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerMindBendMessage implements IMessage<ServerMindBendMessage> {

    private int id;
    private boolean enabled;

    public ServerMindBendMessage() {
    }

    public ServerMindBendMessage(int id, boolean enabled) {
        this.id = id;
        this.enabled = enabled;
    }

    @Override
    public void encode(ServerMindBendMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.id);
        buffer.writeBoolean(message.enabled);
    }

    @Override
    public ServerMindBendMessage decode(PacketBuffer buffer) {
        return new ServerMindBendMessage(buffer.readInt(), buffer.readBoolean());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerMindBendMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                World world = player.getEntityWorld();
                LivingEntity entity = (LivingEntity) world.getEntityByID(message.id);
                if (entity != null) {
                    if (message.enabled) {
                        entity.getPersistentData().putBoolean("HexParticle", true);
                    } else {
                        entity.getPersistentData().remove("HexParticle");
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}