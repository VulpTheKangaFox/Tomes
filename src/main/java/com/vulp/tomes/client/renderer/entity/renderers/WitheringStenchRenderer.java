package com.vulp.tomes.client.renderer.entity.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.entities.projectile.WitheringStenchEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.LlamaSpitModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.IRenderFactory;

// TODO: Model is made of a cluster of cubes that look like a spray, but they're tight. Make a new model and loosen them.
public class WitheringStenchRenderer<T extends WitheringStenchEntity> extends EntityRenderer<T> {

    private static final ResourceLocation WITHERING_STENCH_TEXTURE = new ResourceLocation(Tomes.MODID, "textures/entity/withering_stench.png");
    private final LlamaSpitModel<T> model = new LlamaSpitModel<>();

    public WitheringStenchRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return WITHERING_STENCH_TEXTURE;
    }

    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, (double)0.15F, 0.0D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90.0F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch)));
        this.model.setRotationAngles(entityIn, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityTranslucentCull(WITHERING_STENCH_TEXTURE));
        float alpha = (float) entityIn.getLifespan() / 20.0F;
        this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public static class RenderFactory implements IRenderFactory<WitheringStenchEntity> {
        @Override
        public EntityRenderer<? super WitheringStenchEntity> createRenderFor(EntityRendererManager manager) {
            return new WitheringStenchRenderer<>(manager);
        }
    }

}
