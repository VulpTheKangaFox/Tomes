package com.vulp.tomes.client.renderer.entity.layers;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.client.renderer.RenderTypes;
import com.vulp.tomes.effects.StarryFormEffect;
import com.vulp.tomes.init.EffectInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.OutlineLayerBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class StarryFormLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {

    private static final ResourceLocation EYES = TomesRegistry.location("textures/entity/starry_form_eyes.png");
    private final M MODEL;
    private static final List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj((i) -> RenderType.getEndPortal(i + 1)).collect(ImmutableList.toImmutableList());
    private static final Random rand = new Random();

    public StarryFormLayer(IEntityRenderer<T, M> entityRendererIn) {
        super(entityRendererIn);
        this.MODEL = entityRendererIn.getEntityModel();
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (StarryFormEffect.hasEntity(entitylivingbaseIn)) {
            rand.setSeed(31100L);
            double d0 = entitylivingbaseIn.getPosition().distanceSq(Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView(), true);
            int passes = this.getPasses(d0);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEndPortal(0));
            MODEL.render(matrixStackIn, ivertexbuilder, 0, OverlayTexture.NO_OVERLAY, (rand.nextFloat() * 0.5F + 0.1F) * 0.15F, (rand.nextFloat() * 0.5F + 0.4F) * 0.15F, (rand.nextFloat() * 0.5F + 0.5F) * 0.15F, 1.0F);
            for (int i = 1; i < passes; i++) {
                float j = 2.0F / (float) (18 - i);
                ivertexbuilder = bufferIn.getBuffer(this.getRenderType(i));
                float f = (rand.nextFloat() * 0.5F + 0.1F) * j;
                float f1 = (rand.nextFloat() * 0.5F + 0.4F) * j;
                float f2 = (rand.nextFloat() * 0.5F + 0.5F) * j;
                MODEL.render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0F);
            }
            if (this.MODEL instanceof BipedModel) {
                IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(RenderType.getEyes(EYES));
                ((BipedModel<?>) this.MODEL).bipedHeadwear.render(matrixStackIn, ivertexbuilder1, 15728640, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    protected int getPasses(double distance) {
        if (distance > 36864.0D) {
            return 1;
        } else if (distance > 25600.0D) {
            return 3;
        } else if (distance > 16384.0D) {
            return 5;
        } else if (distance > 9216.0D) {
            return 7;
        } else if (distance > 4096.0D) {
            return 9;
        } else if (distance > 1024.0D) {
            return 11;
        } else if (distance > 576.0D) {
            return 13;
        } else {
            return distance > 256.0D ? 14 : 15;
        }
    }

    public RenderType getRenderType(int stage) {
        return RENDER_TYPES.get(stage);
    }

}
