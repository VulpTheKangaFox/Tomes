package com.vulp.tomes.client.renderer.entity.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.client.renderer.RenderTypes;
import com.vulp.tomes.entities.SpectralSteedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class SpectralSteedRenderer<T extends SpectralSteedEntity> extends MobRenderer<T, HorseModel<T>> {

    private static final ResourceLocation SPECTRAL_STEED_TEXTURE = new ResourceLocation(Tomes.MODID, "textures/entity/spectral_steed.png");

    public SpectralSteedRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new HorseModel<>(0.0F), 0.0F);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return SPECTRAL_STEED_TEXTURE;
    }

    // TODO: Probably solve by using a mixin for the LivingRenderer render, and sliding a slightly modified renderer if LivingEntity uses an interface with an abstract telling us if it should be flashing.
    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (!entityIn.isInvisible()) {
            if (entityIn.isDespawning()) {
                Minecraft minecraft = Minecraft.getInstance();
                PlayerEntity playerEntity = minecraft.player;
                if (playerEntity != null) {
                    boolean flag = this.isVisible(entityIn);
                    boolean flag1 = !flag && !entityIn.isInvisibleToPlayer(minecraft.player);
                    boolean flag2 = minecraft.isEntityGlowing(entityIn);
                    RenderType renderType = func_230496_a_(entityIn, flag, flag1, flag2);
                    if (renderType != null) {
                        // The alpha we need to inject!
                        int alpha = MathHelper.clamp((int)((((MathHelper.cos((float) entityIn.ticksExisted / 5.0F) + 1) * 0.5F) + 0.4) * 255.0F), 0, 255);
                        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                        return;
                    }
                }
            }
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    @Override
    protected RenderType func_230496_a_(T entity, boolean flag1, boolean flag2, boolean flag3) {
        return RenderTypes.getSpectral(SPECTRAL_STEED_TEXTURE, flag3);
    }

    public static class RenderFactory implements IRenderFactory<SpectralSteedEntity> {
        @Override
        public EntityRenderer<? super SpectralSteedEntity> createRenderFor(EntityRendererManager manager) {
            return new SpectralSteedRenderer<>(manager);
        }
    }
}
