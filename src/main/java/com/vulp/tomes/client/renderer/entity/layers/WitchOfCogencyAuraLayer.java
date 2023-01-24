package com.vulp.tomes.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.client.renderer.RenderTypes;
import com.vulp.tomes.client.renderer.entity.models.WitchOfCogencyModel;
import com.vulp.tomes.entities.WitchOfCogencyEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.EnergyLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchOfCogencyAuraLayer<T extends WitchOfCogencyEntity> extends EnergyLayer<T, WitchOfCogencyModel<T>> {
    private static final ResourceLocation TEXTURE = TomesRegistry.location("textures/entity/witch_of_cogency_aura.png");
    private final WitchOfCogencyModel<T> witchModel = new WitchOfCogencyModel<>();

    public WitchOfCogencyAuraLayer(IEntityRenderer<T, WitchOfCogencyModel<T>> p_i50915_1_) {
        super(p_i50915_1_);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.isCharged()) {
            float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
            EntityModel<T> entitymodel = this.func_225635_b_();
            entitymodel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.getEntityModel().copyModelAttributesTo(entitymodel);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderTypes.getEnergyStreaks(this.func_225633_a_(), f * 0.01F));
            entitymodel.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            entitymodel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }
    }

    protected float func_225634_a_(float p_225634_1_) {
        return MathHelper.cos(p_225634_1_ * 0.02F) * 0.2F;
    }

    protected ResourceLocation func_225633_a_() {
        return TEXTURE;
    }

    protected EntityModel<T> func_225635_b_() {
        return this.witchModel;
    }
}
