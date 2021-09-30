package com.vulp.tomes.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class ServerOpenHorseInventoryPacket implements IMessage<ServerOpenHorseInventoryPacket> {

    public ServerOpenHorseInventoryPacket() {
    }

    @Override
    public void encode(ServerOpenHorseInventoryPacket message, PacketBuffer buffer) {
    }

    @Override
    public ServerOpenHorseInventoryPacket decode(PacketBuffer buffer) {
        return new ServerOpenHorseInventoryPacket();
    }

    @Override
    public void handle(ServerOpenHorseInventoryPacket message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientPlayerEntity player = mc.player;
            if (player != null) {
                mc.displayGuiScreen(new InventoryScreen(mc.player));
            }
        });
        supplier.get().setPacketHandled(true);
    }

}