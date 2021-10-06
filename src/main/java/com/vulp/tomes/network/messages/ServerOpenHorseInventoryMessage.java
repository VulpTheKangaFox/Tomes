package com.vulp.tomes.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerOpenHorseInventoryMessage implements IMessage<ServerOpenHorseInventoryMessage> {

    public ServerOpenHorseInventoryMessage() {
    }

    @Override
    public void encode(ServerOpenHorseInventoryMessage message, PacketBuffer buffer) {
    }

    @Override
    public ServerOpenHorseInventoryMessage decode(PacketBuffer buffer) {
        return new ServerOpenHorseInventoryMessage();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerOpenHorseInventoryMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            PlayerEntity player = mc.player;
            if (player != null) {
                mc.displayGuiScreen(new InventoryScreen(mc.player));
            }
        });
        supplier.get().setPacketHandled(true);
    }

}