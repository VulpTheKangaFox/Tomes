package com.vulp.tomes.mixin;

import com.vulp.tomes.effects.StarryFormEffect;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Final private Minecraft mc;

    @Shadow private ClientWorld world;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isSpectator()Z", ordinal = 0), method = "updateCameraAndRender")
    public boolean displayTerrainInStarryForm(ClientPlayerEntity clientPlayerEntity) {
        return clientPlayerEntity.isSpectator() || StarryFormEffect.hasEntity(clientPlayerEntity);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/DimensionRenderInfo;getFogType()Lnet/minecraft/client/world/DimensionRenderInfo$FogType;", ordinal = 0), method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V")
    public DimensionRenderInfo.FogType displayEndSkyInStarryForm(DimensionRenderInfo dimensionRenderInfo) {
        if (mc.player != null) {
            float f1 = mc.player.getSize(mc.player.getPose()).width * 0.8F;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.withSizeAtOrigin(f1, 0.1F, f1).offset(mc.player.getPosX(), mc.player.getPosYEye(), mc.player.getPosZ());
            if (mc.player.getEntityWorld().func_241457_a_(mc.player, axisalignedbb, (state, pos) -> state.isSuffocating(world, pos)).findAny().isPresent()) {
                return DimensionRenderInfo.FogType.END;
            }
        }
        return dimensionRenderInfo.getFogType();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GameSettings;getCloudOption()Lnet/minecraft/client/settings/CloudOption;", ordinal = 0), method = "updateCameraAndRender")
    public CloudOption hideCloudsInStarryForm(GameSettings gameSettings) {
        if (gameSettings.cloudOption == CloudOption.OFF) {
            return CloudOption.OFF;
        } else {
            float f1 = mc.player.getSize(mc.player.getPose()).width * 0.8F;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.withSizeAtOrigin(f1, 0.1F, f1).offset(mc.player.getPosX(), mc.player.getPosYEye(), mc.player.getPosZ());
            return StarryFormEffect.hasEntity(mc.player) && mc.player.getEntityWorld().func_241457_a_(mc.player, axisalignedbb, (state, pos) -> state.isSuffocating(world, pos)).findAny().isPresent() ? CloudOption.OFF : gameSettings.cloudOption;
        }
    }

}
