package com.vulp.tomes.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerEnchantmentClueMessage implements IMessage<ServerEnchantmentClueMessage> {

    private int windowID;
    private int[] list;

    public ServerEnchantmentClueMessage() {
    }

    public ServerEnchantmentClueMessage(int windowID, int[] list) {
        this.windowID = windowID;
        this.list = list;
    }

    @Override
    public void encode(ServerEnchantmentClueMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.windowID);
        buffer.writeVarIntArray(message.list);
    }

    @Override
    public ServerEnchantmentClueMessage decode(PacketBuffer buffer) {
        return new ServerEnchantmentClueMessage(buffer.readInt(), buffer.readVarIntArray());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerEnchantmentClueMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                Container container = player.openContainer;
                if (container.windowId == message.windowID && container instanceof EnchantmentContainer) {
                    ((EnchantmentContainer) container).enchantClue = message.list;
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}