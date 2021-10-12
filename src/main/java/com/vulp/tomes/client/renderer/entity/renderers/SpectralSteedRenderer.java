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
    private static final ResourceLocation SPECTRAL_STEED_FADE_TEXTURE = new ResourceLocation(Tomes.MODID, "textures/entity/spectral_steed_fade.png");

    public SpectralSteedRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new HorseModel<>(0.0F), 0.0F);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        if (entity.getFade()) {
            return SPECTRAL_STEED_FADE_TEXTURE;
        } else return entity.getFade() ? SPECTRAL_STEED_FADE_TEXTURE : SPECTRAL_STEED_TEXTURE;
    }

    // TODO: Probably solve by using a mixin for the LivingRenderer render, and sliding a slightly modified renderer if LivingEntity uses an interface with an abstract telling us if it should be flashing.
    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (!entityIn.isInvisible()) {
            if (entityIn.getFade()) {
                packedLightIn = 15728880;
            }
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    @Override
    protected RenderType func_230496_a_(T entity, boolean flag1, boolean flag2, boolean flag3) {
        return RenderTypes.getSpectral(getEntityTexture(entity), flag3);
    }

    public static class RenderFactory implements IRenderFactory<SpectralSteedEntity> {
        @Override
        public EntityRenderer<? super SpectralSteedEntity> createRenderFor(EntityRendererManager manager) {
            return new SpectralSteedRenderer<>(manager);
        }
    }
}
