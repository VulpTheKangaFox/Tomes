package com.vulp.tomes.client.renderer.entity.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.client.renderer.entity.models.TamedSpiderModel;
import com.vulp.tomes.entities.TamedSpiderEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class TamedSpiderRenderer<T extends TamedSpiderEntity> extends MobRenderer<T, TamedSpiderModel<T>> {

    private static final ResourceLocation TAMED_SPIDER_TEXTURE = TomesRegistry.location("textures/entity/tamed_spider.png");
    private static final RenderType RENDER_TYPE = RenderType.getEyes(TomesRegistry.location("textures/entity/tamed_spider_eyes.png"));

    public TamedSpiderRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new TamedSpiderModel<>(), 0.8F);
        this.addLayer(new TamedSpiderEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return TAMED_SPIDER_TEXTURE;
    }

    @Override
    protected float getDeathMaxRotation(T entityLivingBaseIn) {
        return 180.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class TamedSpiderEyesLayer<T extends TamedSpiderEntity, M extends TamedSpiderModel<T>> extends AbstractEyesLayer<T, M> {

        public TamedSpiderEyesLayer(IEntityRenderer<T, M> rendererIn) {
            super(rendererIn);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.getRenderType());
            float color = entity.getHealth() / entity.getMaxHealth();
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, color, color, color, 1.0F);
        }

        @Override
        public RenderType getRenderType() {
            return RENDER_TYPE;
        }
    }

    public static class RenderFactory implements IRenderFactory<TamedSpiderEntity> {
        @Override
        public EntityRenderer<? super TamedSpiderEntity> createRenderFor(EntityRendererManager manager) {
            return new TamedSpiderRenderer<>(manager);
        }
    }

}
