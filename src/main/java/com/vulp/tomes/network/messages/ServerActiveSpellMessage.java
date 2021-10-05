package com.vulp.tomes.network.messages;

import com.vulp.tomes.items.TomeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerActiveSpellMessage implements IMessage<ServerActiveSpellMessage> {

    private ItemStack bookStack;
    private boolean mainHand;

    public ServerActiveSpellMessage() {
    }

    public ServerActiveSpellMessage(ItemStack bookStack, boolean mainHand) {
        this.bookStack = bookStack;
        this.mainHand = mainHand;
    }

    @Override
    public void encode(ServerActiveSpellMessage message, PacketBuffer buffer) {
        buffer.writeItemStack(message.bookStack);
        buffer.writeBoolean(message.mainHand);
    }

    @Override
    public ServerActiveSpellMessage decode(PacketBuffer buffer) {
        return new ServerActiveSpellMessage(buffer.readItemStack(), buffer.readBoolean());
    }

    @Override
    public void handle(ServerActiveSpellMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                World world = Minecraft.getInstance().player.getEntityWorld();
                TomeItem.clientCastActiveSpell(world, player, new ItemStack[]{message.bookStack}, mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}