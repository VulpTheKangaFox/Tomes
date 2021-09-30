package com.vulp.tomes.client.renderer.entity.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.client.renderer.RenderTypes;
import com.vulp.tomes.entities.SpectralSteedEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.util.ResourceLocation;
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

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (!entityIn.isInvisible()) {
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
