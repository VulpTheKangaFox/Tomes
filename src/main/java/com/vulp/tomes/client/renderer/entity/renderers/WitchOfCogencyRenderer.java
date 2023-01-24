package com.vulp.tomes.client.renderer.entity.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.client.renderer.entity.layers.WitchOfCogencyAuraLayer;
import com.vulp.tomes.client.renderer.entity.layers.WitchOfCogencyHeldItemLayer;
import com.vulp.tomes.client.renderer.entity.models.WitchOfCogencyModel;
import com.vulp.tomes.entities.WitchOfCogencyEntity;
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
public class WitchOfCogencyRenderer<T extends WitchOfCogencyEntity> extends MobRenderer<T, WitchOfCogencyModel<T>> {
    private static final ResourceLocation WITCH_TEXTURES_1 = TomesRegistry.location("textures/entity/witch_of_cogency_1.png");
    private static final ResourceLocation WITCH_TEXTURES_2 = TomesRegistry.location("textures/entity/witch_of_cogency_2.png");
    private static final RenderType WITCH_EYES = RenderType.getEyes(TomesRegistry.location("textures/entity/witch_of_cogency_eyes.png"));
    private static final ResourceLocation WITCH_INVULNERABLE_TEXTURE = TomesRegistry.location("textures/entity/witch_of_cogency_invulnerable.png");

    public WitchOfCogencyRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new WitchOfCogencyModel<>(), 0.5F);
        this.addLayer(new WitchOfCogencyHeldItemLayer<>(this));
        this.addLayer(new WitchOfCogencyAuraLayer<>(this));
        this.addLayer(new WitchOfCogencyEyesLayer<>(this));
    }

    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        this.entityModel.setHoldingItem(!entityIn.getHeldItemMainhand().isEmpty());
        this.entityModel.setFallSpeed(entityIn.getFallSpeed());
        this.entityModel.setCasting(entityIn.isCasting());
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(T entity) {
        if (entity.getInvulTime() > 0) {
            return WITCH_INVULNERABLE_TEXTURE;
        } else return entity.isFirstStage() ? WITCH_TEXTURES_1 : WITCH_TEXTURES_2;
    }

    protected void preRenderCallback(T entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        int i = entitylivingbaseIn.getInvulTime();
        if (i > 0 && entitylivingbaseIn.isFirstStage()) {
            f -= ((float)i - partialTickTime) / 220.0F * 0.5F;
        }

        matrixStackIn.scale(f, f, f);
    }

    public static class RenderFactory implements IRenderFactory<WitchOfCogencyEntity> {
        @Override
        public EntityRenderer<? super WitchOfCogencyEntity> createRenderFor(EntityRendererManager manager) {
            return new WitchOfCogencyRenderer<>(manager);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class WitchOfCogencyEyesLayer<T extends WitchOfCogencyEntity, M extends WitchOfCogencyModel<T>> extends AbstractEyesLayer<T, M> {

        public WitchOfCogencyEyesLayer(IEntityRenderer<T, M> rendererIn) {
            super(rendererIn);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isFirstStage()) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.getRenderType());
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, 7864320, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType getRenderType() {
            return WITCH_EYES;
        }
    }

}