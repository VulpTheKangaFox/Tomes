package com.vulp.tomes.network.messages;

import com.vulp.tomes.init.ParticleInit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class ServerGobletCraftMessage implements IMessage<ServerGobletCraftMessage> {

    private BlockPos pos;

    public ServerGobletCraftMessage() {
    }

    public ServerGobletCraftMessage(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void encode(ServerGobletCraftMessage message, PacketBuffer buffer) {
        buffer.writeBlockPos(message.pos);
    }

    @Override
    public ServerGobletCraftMessage decode(PacketBuffer buffer) {
        return new ServerGobletCraftMessage(buffer.readBlockPos());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerGobletCraftMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            World world = mc.world;
            if (world != null) {
                Random rand = new Random();
                world.playSound(message.pos.getX(), message.pos.getY(), message.pos.getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                for (int i = 0; i < new Random().nextInt(4) + 6; i++) {
                    float j = rand.nextFloat() / 2.0F;
                    float k = -0.125F + (j / 2.0F);
                    world.addParticle(ParticleInit.goblet_particle, message.pos.getX() + 0.3F + (rand.nextFloat() / 2.5F) + k, message.pos.getY() + 0.85F + j, message.pos.getZ() + 0.3F + (rand.nextFloat() / 2.5F)+ k, 0.0F, 0.0D, 0.0F);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}