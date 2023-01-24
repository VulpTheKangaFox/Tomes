package com.vulp.tomes.network.messages;

import com.vulp.tomes.entities.WitchOfCogencyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerWitchOfCogencyCastMessage implements IMessage<ServerWitchOfCogencyCastMessage> {

    private int entityID;
    private boolean isCasting;

    public ServerWitchOfCogencyCastMessage() {
    }

    public ServerWitchOfCogencyCastMessage(int entityID, boolean isCasting) {
        this.entityID = entityID;
        this.isCasting = isCasting;
    }

    @Override
    public void encode(ServerWitchOfCogencyCastMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.entityID);
        buffer.writeBoolean(message.isCasting);
    }

    @Override
    public ServerWitchOfCogencyCastMessage decode(PacketBuffer buffer) {
        return new ServerWitchOfCogencyCastMessage(buffer.readInt(), buffer.readBoolean());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerWitchOfCogencyCastMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                World world = Minecraft.getInstance().world;
                if (world != null) {
                    LivingEntity entity = (LivingEntity) world.getEntityByID(message.entityID);
                    if (entity instanceof WitchOfCogencyEntity) {
                        ((WitchOfCogencyEntity) entity).setCasting(message.isCasting);
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}