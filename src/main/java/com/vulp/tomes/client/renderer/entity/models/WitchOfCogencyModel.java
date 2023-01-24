package com.vulp.tomes.client.renderer.entity.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.entities.WitchOfCogencyEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchOfCogencyModel<T extends WitchOfCogencyEntity> extends EntityModel<T> {

    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer nose;
    private final ModelRenderer hat;
    private final ModelRenderer cape;
    private final ModelRenderer arms1;
    private final ModelRenderer arms2;
    private final ModelRenderer left_arm;
    private final ModelRenderer right_arm;
    private final ModelRenderer left_leg;
    private final ModelRenderer right_leg;
    private boolean holdingItem;
    private float fallSpeed;
    private boolean isCasting;

    public WitchOfCogencyModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body.setTextureOffset(0, 28).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 12.0F, 6.0F, 0.0F, true);
        this.body.setTextureOffset(28, 28).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 18.0F, 6.0F, 0.5F, false);
        this.body.setTextureOffset(0, 14).addBox(-6.0F, -25.0F, -5.0F, 12.0F, 4.0F, 10.0F, 0.0F, false);

        this.head = new ModelRenderer(this);
        this.head.setRotationPoint(0.0F, -24.0F, 0.0F);
        this.body.addChild(head);
        this.head.setTextureOffset(0, 0).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, 0.0F, false);
        this.head.setTextureOffset(32, 0).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, 0.5F, false);

        this.nose = new ModelRenderer(this);
        this.nose.setRotationPoint(0.0F, -2.0F, -4.0F);
        this.head.addChild(nose);
        this.nose.setTextureOffset(0, 2).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
        this.nose.setTextureOffset(0, 0).addBox(0.5F, 0.0F, -2.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        this.hat = new ModelRenderer(this);
        this.hat.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.head.addChild(hat);
        this.hat.setTextureOffset(42, 40).addBox(-7.0F, -2.0F, -7.0F, 14.0F, 2.0F, 14.0F, 0.0F, false);

        ModelRenderer hat_part_1 = new ModelRenderer(this);
        hat_part_1.setRotationPoint(0.0F, -14.0F, 1.0F);
        this.hat.addChild(hat_part_1);
        setRotationAngle(hat_part_1, -0.5574F, -0.0779F, -0.512F);
        hat_part_1.setTextureOffset(84, 44).addBox(-1.7034F, -2.7193F, -0.3437F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        ModelRenderer hat_part_2 = new ModelRenderer(this);
        hat_part_2.setRotationPoint(2.0F, -10.0F, -2.0F);
        this.hat.addChild(hat_part_2);
        setRotationAngle(hat_part_2, -0.3054F, 0.0F, -0.2618F);
        hat_part_2.setTextureOffset(84, 35).addBox(-3.6F, -5.0F, 0.2F, 4.0F, 5.0F, 4.0F, 0.0F, false);

        ModelRenderer hat_part_3 = new ModelRenderer(this);
        hat_part_3.setRotationPoint(0.0F, -6.0F, -4.0F);
        this.hat.addChild(hat_part_3);
        setRotationAngle(hat_part_3, -0.1309F, 0.0F, 0.0F);
        hat_part_3.setTextureOffset(80, 8).addBox(-3.2F, -5.0F, 0.4F, 7.0F, 5.0F, 7.0F, 0.0F, false);

        ModelRenderer hat_part_4 = new ModelRenderer(this);
        hat_part_4.setRotationPoint(-5.0F, -2.0F, 0.0F);
        this.hat.addChild(hat_part_4);
        setRotationAngle(hat_part_4, 0.0F, 0.0F, 0.0873F);
        hat_part_4.setTextureOffset(72, 20).addBox(0.0F, -5.0F, -5.0F, 10.0F, 5.0F, 10.0F, 0.0F, false);

        this.cape = new ModelRenderer(this);
        this.cape.setRotationPoint(0.0F, -25.0F, 5.0F);
        this.body.addChild(cape);
        setRotationAngle(cape, 0.0873F, 0.0F, 0.0F);
        this.cape.setTextureOffset(100, 36).addBox(-6.0F, 0.0F, -2.0F, 12.0F, 21.0F, 2.0F, 0.025F, false);

        this.arms1 = new ModelRenderer(this);
        this.arms1.setRotationPoint(0.0F, -22.0F, -1.0F);
        this.body.addChild(arms1);
        setRotationAngle(arms1, -0.7854F, 0.0F, 0.0F);
        this.arms1.setTextureOffset(56, 0).addBox(4.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, true);
        this.arms1.setTextureOffset(72, 0).addBox(-8.0F, 2.0F, -2.0F, 16.0F, 4.0F, 4.0F, 0.0F, false);
        this.arms1.setTextureOffset(56, 0).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        this.arms2 = new ModelRenderer(this);
        this.arms2.setRotationPoint(0.0F, -21.0F, 0.0F);
        this.body.addChild(arms2);


        this.left_arm = new ModelRenderer(this);
        this.left_arm.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.arms2.addChild(left_arm);
        this.left_arm.setTextureOffset(56, 24).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        this.left_arm.setTextureOffset(64, 8).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);

        this.right_arm = new ModelRenderer(this);
        this.right_arm.setRotationPoint(4.0F, 0.0F, 0.0F);
        this.arms2.addChild(right_arm);
        this.right_arm.setTextureOffset(56, 24).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);
        this.right_arm.setTextureOffset(64, 8).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, true);

        this.left_leg = new ModelRenderer(this);
        this.left_leg.setRotationPoint(-2.0F, -12.0F, 0.0F);
        this.body.addChild(left_leg);
        this.left_leg.setTextureOffset(108, 4).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        this.right_leg = new ModelRenderer(this);
        this.right_leg.setRotationPoint(2.0F, -12.0F, 0.0F);
        this.body.addChild(right_leg);
        this.right_leg.setTextureOffset(112, 20).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(WitchOfCogencyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = entity.shouldFoldArms();
        this.arms1.showModel = flag;
        this.arms2.showModel = !flag;

        // VillagerModel
        this.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        this.head.rotateAngleX = headPitch * ((float) Math.PI / 180F);
        this.right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.right_leg.rotateAngleY = 0.0F;
        this.left_leg.rotateAngleY = 0.0F;

        // WitchModel
        this.nose.setRotationPoint(0.0F, -2.0F, -4.0F);
        float i = 0.01F * (float) (entity.getEntityId() % 10);
        this.nose.rotateAngleX = MathHelper.sin((float) entity.ticksExisted * i) * 4.5F * ((float) Math.PI / 180F);
        this.nose.rotateAngleY = 0.0F;
        this.nose.rotateAngleZ = MathHelper.cos((float) entity.ticksExisted * i) * 2.5F * ((float) Math.PI / 180F);
        if (this.holdingItem) {
            this.nose.setRotationPoint(0.0F, -2.0F, -3.5F);
            this.nose.rotateAngleX = -0.9F;
        }
        this.cape.rotateAngleX = MathHelper.clamp(0.0873F + (fallSpeed * 80.0F * ((float) Math.PI / 180F)), 0.0873F, (float) Math.PI);
        if (!flag) {
            if (this.isCasting) {
                this.right_arm.rotationPointX = -4.0F;
                this.left_arm.rotationPointX = 4.0F;
                this.right_arm.rotateAngleX = MathHelper.cos(ageInTicks * 0.5662F) * 0.25F;
                this.left_arm.rotateAngleX = MathHelper.cos(ageInTicks * 0.5662F) * 0.25F;
                this.right_arm.rotateAngleZ = 2.3561945F;
                this.left_arm.rotateAngleZ = -2.3561945F;
            } else {
                this.right_arm.rotationPointX = 4.0F;
                this.left_arm.rotationPointX = -4.0F;
                this.right_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
                this.left_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
                this.right_arm.rotateAngleZ = 0.0F;
                this.left_arm.rotateAngleZ = 0.0F;
            }
            this.right_arm.rotateAngleY = 0.0F;
            this.left_arm.rotateAngleY = 0.0F;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        this.body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public ModelRenderer getHead() {
        return this.head;
    }

    public ModelRenderer getNose() {
        return this.nose;
    }

    public void setHoldingItem(boolean holding) {
        this.holdingItem = holding;
    }

    public void setFallSpeed(float speed) {
        this.fallSpeed = speed;
    }

    public void setCasting(boolean isCasting) {
        this.isCasting = isCasting;
    }
}