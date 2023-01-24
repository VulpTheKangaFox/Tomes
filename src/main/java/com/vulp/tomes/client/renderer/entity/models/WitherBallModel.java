package com.vulp.tomes.client.renderer.entity.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.entities.WitherBallEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.Random;

public class WitherBallModel extends EntityModel<WitherBallEntity> {
    private final ModelRenderer ball;
    private final ModelRenderer trail;

    public WitherBallModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.ball = new ModelRenderer(this);
        this.ball.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.ball.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        this.trail = new ModelRenderer(this);
        this.trail.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ball.addChild(trail);
        this.trail.setTextureOffset(22, 6).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 10.0F, 1.0F, false);
        this.trail.setTextureOffset(0, 16).addBox(-5.0F, -5.0F, 2.0F, 10.0F, 10.0F, 0.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(WitherBallEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        this.ball.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void rotateTrail(boolean clockwise) {
        this.trail.rotateAngleZ += clockwise ? 0.02 : -0.02;
    }

    public void setRotationAngle(float x, float y, float z) {
        this.ball.rotateAngleX = x;
        this.ball.rotateAngleY = y;
        this.ball.rotateAngleZ = z;
    }
}