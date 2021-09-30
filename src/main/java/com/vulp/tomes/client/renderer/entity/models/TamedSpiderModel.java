package com.vulp.tomes.client.renderer.entity.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.entities.TamedSpiderEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TamedSpiderModel<T extends TamedSpiderEntity> extends SegmentedModel<T> {
    private final ModelRenderer spiderHead;
    private final ModelRenderer spiderNeck;
    private final ModelRenderer spiderBody;
    private final ModelRenderer spiderLeg1;
    private final ModelRenderer spiderLeg2;
    private final ModelRenderer spiderLeg3;
    private final ModelRenderer spiderLeg4;
    private final ModelRenderer spiderLeg5;
    private final ModelRenderer spiderLeg6;
    private final ModelRenderer spiderLeg7;
    private final ModelRenderer spiderLeg8;

    public TamedSpiderModel() {
        this.spiderHead = new ModelRenderer(this, 32, 4);
        this.spiderHead.addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, 0.0F);
        this.spiderHead.setRotationPoint(0.0F, 15.0F, -3.0F);
        this.spiderNeck = new ModelRenderer(this, 0, 0);
        this.spiderNeck.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F);
        this.spiderNeck.setRotationPoint(0.0F, 15.0F, 0.0F);
        this.spiderBody = new ModelRenderer(this, 0, 12);
        this.spiderBody.addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F, 0.0F);
        this.spiderBody.setRotationPoint(0.0F, 15.0F, 9.0F);
        this.spiderLeg1 = new ModelRenderer(this, 18, 0);
        this.spiderLeg1.addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
        this.spiderLeg1.setRotationPoint(-4.0F, 15.0F, 2.0F);
        this.spiderLeg2 = new ModelRenderer(this, 18, 0);
        this.spiderLeg2.addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
        this.spiderLeg2.setRotationPoint(4.0F, 15.0F, 2.0F);
        this.spiderLeg3 = new ModelRenderer(this, 18, 0);
        this.spiderLeg3.addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
        this.spiderLeg3.setRotationPoint(-4.0F, 15.0F, 1.0F);
        this.spiderLeg4 = new ModelRenderer(this, 18, 0);
        this.spiderLeg4.addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
        this.spiderLeg4.setRotationPoint(4.0F, 15.0F, 1.0F);
        this.spiderLeg5 = new ModelRenderer(this, 18, 0);
        this.spiderLeg5.addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
        this.spiderLeg5.setRotationPoint(-4.0F, 15.0F, 0.0F);
        this.spiderLeg6 = new ModelRenderer(this, 18, 0);
        this.spiderLeg6.addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
        this.spiderLeg6.setRotationPoint(4.0F, 15.0F, 0.0F);
        this.spiderLeg7 = new ModelRenderer(this, 18, 0);
        this.spiderLeg7.addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
        this.spiderLeg7.setRotationPoint(-4.0F, 15.0F, -1.0F);
        this.spiderLeg8 = new ModelRenderer(this, 18, 0);
        this.spiderLeg8.addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
        this.spiderLeg8.setRotationPoint(4.0F, 15.0F, -1.0F);
    }

    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.spiderHead, this.spiderNeck, this.spiderBody, this.spiderLeg1, this.spiderLeg2, this.spiderLeg3, this.spiderLeg4, this.spiderLeg5, this.spiderLeg6, this.spiderLeg7, this.spiderLeg8);
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.spiderHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        this.spiderHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        float f = ((float)Math.PI / 4F);
        float f2 = ((float)Math.PI / 8F);
        float rotPointY;
        if (!entityIn.isEntitySleeping()) {
            rotPointY = 15.0F;
            float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
            float f4 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * limbSwingAmount;
            float f5 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
            float f6 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
            float f7 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
            float f8 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float)Math.PI) * 0.4F) * limbSwingAmount;
            float f9 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
            float f10 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
            this.spiderLeg1.rotateAngleY = f;
            this.spiderLeg2.rotateAngleY = -f;
            this.spiderLeg3.rotateAngleY = f2;
            this.spiderLeg4.rotateAngleY = -f2;
            this.spiderLeg5.rotateAngleY = -f2;
            this.spiderLeg6.rotateAngleY = f2;
            this.spiderLeg7.rotateAngleY = -f;
            this.spiderLeg8.rotateAngleY = f;
            this.spiderLeg1.rotateAngleZ = -f;
            this.spiderLeg2.rotateAngleZ = f;
            this.spiderLeg3.rotateAngleZ = -0.58119464F;
            this.spiderLeg4.rotateAngleZ = 0.58119464F;
            this.spiderLeg5.rotateAngleZ = -0.58119464F;
            this.spiderLeg6.rotateAngleZ = 0.58119464F;
            this.spiderLeg7.rotateAngleZ = -f;
            this.spiderLeg8.rotateAngleZ = f;
            this.spiderLeg1.rotateAngleY += f3;
            this.spiderLeg2.rotateAngleY += -f3;
            this.spiderLeg3.rotateAngleY += f4;
            this.spiderLeg4.rotateAngleY += -f4;
            this.spiderLeg5.rotateAngleY += f5;
            this.spiderLeg6.rotateAngleY += -f5;
            this.spiderLeg7.rotateAngleY += f6;
            this.spiderLeg8.rotateAngleY += -f6;
            this.spiderLeg1.rotateAngleZ += f7;
            this.spiderLeg2.rotateAngleZ += -f7;
            this.spiderLeg3.rotateAngleZ += f8;
            this.spiderLeg4.rotateAngleZ += -f8;
            this.spiderLeg5.rotateAngleZ += f9;
            this.spiderLeg6.rotateAngleZ += -f9;
            this.spiderLeg7.rotateAngleZ += f10;
            this.spiderLeg8.rotateAngleZ += -f10;
        } else {
            rotPointY = 19.25F;
            float j1 = ((float)Math.PI / 3F);
            float j2 = ((float)Math.PI / 7F);
            this.spiderLeg1.rotateAngleY = j1;
            this.spiderLeg2.rotateAngleY = -j1;
            this.spiderLeg3.rotateAngleY = j2;
            this.spiderLeg4.rotateAngleY = -j2;
            this.spiderLeg5.rotateAngleY = -j2;
            this.spiderLeg6.rotateAngleY = j2;
            this.spiderLeg7.rotateAngleY = -j1;
            this.spiderLeg8.rotateAngleY = j1;
            this.spiderLeg1.rotateAngleZ = -j2;
            this.spiderLeg2.rotateAngleZ = j2;
            this.spiderLeg3.rotateAngleZ = -0.3F;
            this.spiderLeg4.rotateAngleZ = 0.3F;
            this.spiderLeg5.rotateAngleZ = -0.3F;
            this.spiderLeg6.rotateAngleZ = 0.3F;
            this.spiderLeg7.rotateAngleZ = -j2;
            this.spiderLeg8.rotateAngleZ = j2;
        }
        this.spiderHead.rotationPointY = rotPointY;
        this.spiderNeck.rotationPointY = rotPointY;
        this.spiderBody.rotationPointY = rotPointY;
        this.spiderLeg1.rotationPointY = rotPointY;
        this.spiderLeg2.rotationPointY = rotPointY;
        this.spiderLeg3.rotationPointY = rotPointY;
        this.spiderLeg4.rotationPointY = rotPointY;
        this.spiderLeg5.rotationPointY = rotPointY;
        this.spiderLeg6.rotationPointY = rotPointY;
        this.spiderLeg7.rotationPointY = rotPointY;
        this.spiderLeg8.rotationPointY = rotPointY;
    }

}