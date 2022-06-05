package com.vulp.tomes.network.messages;

import com.vulp.tomes.particles.ClientParticleLogic;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// Using this will ensure particles are shown for all players even though triggered only on one player's end normally.
public class ServerSimpleParticlePassthroughMessage implements IMessage<ServerSimpleParticlePassthroughMessage> {

    private int ordinal; // Allocated which particle function is triggered.
    private CompoundNBT data; // Attach any data before constructing the message to this. Afterwards, if ordinal is correct it will grab the data it needs from this same compound.

    public ServerSimpleParticlePassthroughMessage() {
    }

    public ServerSimpleParticlePassthroughMessage(int ordinal, CompoundNBT data) {
        this.ordinal = ordinal;
        this.data = data;
    }

    @Override
    public void encode(ServerSimpleParticlePassthroughMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.ordinal);
        buffer.writeCompoundTag(message.data);
    }

    @Override
    public ServerSimpleParticlePassthroughMessage decode(PacketBuffer buffer) {
        return new ServerSimpleParticlePassthroughMessage(buffer.readInt(), buffer.readCompoundTag());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerSimpleParticlePassthroughMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> ClientParticleLogic.receiveRequest(message.ordinal, message.data));
        supplier.get().setPacketHandled(true);
    }

}