package com.vulp.tomes.network.messages;

import com.vulp.tomes.effects.MultiJumpEffect;
import com.vulp.tomes.init.EffectInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientJumpPressedMessage implements IMessage<ClientJumpPressedMessage> {

    private UUID playerID;

    public ClientJumpPressedMessage() {
    }

    public ClientJumpPressedMessage(UUID playerID) {
        this.playerID = playerID;
    }

    @Override
    public void encode(ClientJumpPressedMessage message, PacketBuffer buffer) {
        buffer.writeUniqueId(message.playerID);
    }

    @Override
    public ClientJumpPressedMessage decode(PacketBuffer buffer) {
        return new ClientJumpPressedMessage(buffer.readUniqueId());
    }

    @Override
    public void handle(ClientJumpPressedMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayerEntity sender = supplier.get().getSender();
            if (sender != null) {
                PlayerEntity player = sender.getServerWorld().getPlayerByUuid(message.playerID);
                if (player != null) {
                    if (player.isPotionActive(EffectInit.multi_jump)) {
                        MultiJumpEffect.doAirJump(player);
                    } else {
                        MultiJumpEffect.removeJumper(player);
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}
