package com.vulp.tomes.network.messages;

import com.vulp.tomes.init.ParticleInit;
import com.vulp.tomes.network.TomesPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Random;
import java.util.function.Supplier;

public class ServerProjDeflectMessage implements IMessage<ServerProjDeflectMessage> {

    private int victimID;
    private int projID;

    public ServerProjDeflectMessage() {
    }

    public ServerProjDeflectMessage(int victimID, int projID) {
        this.victimID = victimID;
        this.projID = projID;
    }

    @Override
    public void encode(ServerProjDeflectMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.victimID);
        buffer.writeInt(message.projID);
    }

    @Override
    public ServerProjDeflectMessage decode(PacketBuffer buffer) {
        return new ServerProjDeflectMessage(buffer.readInt(), buffer.readInt());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(ServerProjDeflectMessage message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                World world = player.world;
                Random rand = new Random();
                ProjectileEntity proj = (ProjectileEntity) world.getEntityByID(message.projID);
                Entity victim = world.getEntityByID(message.victimID);
                if (proj != null) {
                    Vector3d vec = proj.getMotion();
                    Vector3d vec2 = vec.inverse();
                    proj.setVelocity(vec2.getX() * 0.75, vec2.getY() * 0.75, vec2.getZ() * 0.75);
                    proj.rotationPitch = MathHelper.wrapDegrees(proj.rotationPitch + 180);
                    proj.rotationYaw = MathHelper.wrapDegrees(proj.rotationYaw + 180);
                    boolean flag = false;
                    if (proj instanceof DamagingProjectileEntity) {
                        flag = true;
                        DamagingProjectileEntity damageProj = (DamagingProjectileEntity) proj;
                        damageProj.accelerationX = -damageProj.accelerationX * 0.75;
                        damageProj.accelerationY = -damageProj.accelerationY * 0.75;
                        damageProj.accelerationZ = -damageProj.accelerationZ * 0.75;
                    }
                    proj.setShooter(victim);
                    proj.velocityChanged = true;
                    if (world.isRemote) {
                        Vector3d pos = proj.getPositionVec();
                        proj.world.addParticle(ParticleInit.wind_deflect, (float) (pos.getX() + rand.nextFloat()) - 0.5F, (float) (pos.getY() + rand.nextFloat()), (float) (pos.getZ() + rand.nextFloat()) - 0.5F, proj.rotationPitch, flag ? proj.rotationYaw : -proj.rotationYaw, 1.2D);
                    }
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
