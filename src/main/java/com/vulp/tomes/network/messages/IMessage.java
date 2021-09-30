package com.vulp.tomes.network.messages;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessage<T> {

    void encode(T message, PacketBuffer buffer);

    T decode(PacketBuffer buffer);

    void handle(T message, Supplier<NetworkEvent.Context> supplier);

}
