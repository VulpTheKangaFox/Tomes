package com.vulp.tomes.client.renderer.entity.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.client.renderer.entity.models.WitherBallModel;
import com.vulp.tomes.entities.WitherBallEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class WitherBallRenderer<T extends WitherBallEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE = TomesRegistry.location("textures/entity/wither_ball.png");
    private final WitherBallModel witherBallModel = new WitherBallModel();
    private final boolean clockwise;

    public WitherBallRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.clockwise = new Random().nextBoolean();
    }

    protected int getBlockLight(T entityIn, BlockPos pos) {
        return 15;
    }

    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        float f = MathHelper.rotLerp(entityIn.prevRotationYaw, entityIn.rotationYaw, partialTicks);
        float f1 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(this.getEntityTexture(entityIn)));
        this.witherBallModel.setRotationAngle(0.0F, f * ((float)Math.PI / 180F), f1 * ((float)Math.PI / 180F));
        matrixStackIn.translate(0.0D, -1.5D, 0.0D);
        this.witherBallModel.rotateTrail(entityIn.getEntityId() % 2 == 0);
        this.witherBallModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }


    public ResourceLocation getEntityTexture(T entity) {
        return TEXTURE;
    }

    public static class RenderFactory implements IRenderFactory<WitherBallEntity> {
        @Override
        public EntityRenderer<? super WitherBallEntity> createRenderFor(EntityRendererManager manager) {
            return new WitherBallRenderer<>(manager);
        }
    }

}