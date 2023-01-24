package com.vulp.tomes.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.client.renderer.entity.models.WingedModel;
import com.vulp.tomes.init.EffectInit;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WingedLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    private static final ResourceLocation TEXTURE = TomesRegistry.location("textures/entity/metamorphosis.png");
    private final WingedModel<T> model = new WingedModel<>();

    public WingedLayer(IEntityRenderer<T, M> rendererIn) {
        super(rendererIn);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (shouldRender(entitylivingbaseIn)) {
            matrixStackIn.push();
            matrixStackIn.translate(0.0D, 0.0D, entitylivingbaseIn.hasItemInSlot(EquipmentSlotType.CHEST) ? 0.200D : 0.125D);
            this.getEntityModel().copyModelAttributesTo(this.model);
            this.model.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            IVertexBuilder ivertexbuilder = ItemRenderer.getArmorVertexBuilder(bufferIn, RenderType.getArmorCutoutNoCull(TEXTURE), false, false);
            this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
        }
    }

    public boolean shouldRender(T entity) {
        return entity.isPotionActive(EffectInit.winged) && entity.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() != Items.ELYTRA;
    }
}