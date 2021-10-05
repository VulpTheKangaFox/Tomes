package com.vulp.tomes.network.messages;

import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.items.TomeItem;
import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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