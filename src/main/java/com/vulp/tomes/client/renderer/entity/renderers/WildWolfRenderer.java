package com.vulp.tomes.client.renderer.entity.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.client.renderer.RenderTypes;
import com.vulp.tomes.client.renderer.entity.models.TamedSpiderModel;
import com.vulp.tomes.entities.TamedSpiderEntity;
import com.vulp.tomes.entities.WildWolfEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class WildWolfRenderer extends WolfRenderer {

    // TODO: Maybe an eye layer for glowy magic eyes?

    private static final ResourceLocation WILD_WOLF_TEXTURE = new ResourceLocation(Tomes.MODID, "textures/entity/wild_wolf.png");
    private static final ResourceLocation WILD_WOLF_FADE_TEXTURE = new ResourceLocation(Tomes.MODID, "textures/entity/wild_wolf_fade.png");
    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(Tomes.MODID, "textures/entity/wild_wolf_eyes.png"));

    public WildWolfRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.layerRenderers.remove(this.layerRenderers.size() - 1);
        this.addLayer(new WildWolfEyesLayer<>(this));
    }

    public ResourceLocation getEntityTexture(WolfEntity entity) {
        return ((WildWolfEntity)entity).getFade() ? WILD_WOLF_FADE_TEXTURE : WILD_WOLF_TEXTURE;
    }

    @Nullable
    @Override
    protected RenderType func_230496_a_(WolfEntity entity, boolean flag1, boolean flag2, boolean flag3) {
        return ((WildWolfEntity)entity).getFade() ? RenderType.getEntityTranslucentCull(getEntityTexture(entity)) : super.func_230496_a_(entity, flag1, flag2, flag3);
    }

    @OnlyIn(Dist.CLIENT)
    public static class WildWolfEyesLayer<T extends WolfEntity, M extends WolfModel<T>> extends AbstractEyesLayer<T, M> {

        public WildWolfEyesLayer(IEntityRenderer<T, M> rendererIn) {
            super(rendererIn);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.getRenderType());
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, 7864320, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        @Override
        public RenderType getRenderType() {
            return RENDER_TYPE;
        }
    }

    public static class RenderFactory implements IRenderFactory<WildWolfEntity> {
        @Override
        public EntityRenderer<? super WildWolfEntity> createRenderFor(EntityRendererManager manager) {
            return new WildWolfRenderer(manager);
        }
    }

}
