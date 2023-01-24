package com.vulp.tomes.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vulp.tomes.client.renderer.entity.models.WitchOfCogencyModel;
import com.vulp.tomes.entities.WitchOfCogencyEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchOfCogencyHeldItemLayer<T extends WitchOfCogencyEntity> extends CrossedArmsItemLayer<T, WitchOfCogencyModel<T>> {
    public WitchOfCogencyHeldItemLayer(IEntityRenderer<T, WitchOfCogencyModel<T>> model) {
        super(model);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T witch, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = witch.getHeldItemMainhand();
        matrixStackIn.push();
        if (itemstack.getItem() == Items.POTION) {
            this.getEntityModel().getHead().translateRotate(matrixStackIn);
            this.getEntityModel().getNose().translateRotate(matrixStackIn);
            matrixStackIn.translate(0.0625D, 0.25D, 0.0D);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(140.0F));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(10.0F));
            matrixStackIn.translate(0.2D, (double)1.2F, (double)0.05F);
        }

        super.render(matrixStackIn, bufferIn, packedLightIn, witch, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        matrixStackIn.pop();
    }
}