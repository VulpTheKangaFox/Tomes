package com.vulp.tomes.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vulp.tomes.client.renderer.TomeTextureCache;
import com.vulp.tomes.items.TomeIndex;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class TomeTileEntityRenderer extends ItemStackTileEntityRenderer {

    TomeTextureCache cache = new TomeTextureCache();

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack ms, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (this.cache.isEmpty()) {
            this.cache.initTextures();
        }
        TomeItem item = (TomeItem) stack.getItem();
        int cooldownPixels = MathHelper.clamp(TomeItem.getCooldown(stack) / TomeItem.getCooldownMax(stack), 0, 15);
        Matrix4f matrix4f = ms.getLast().getMatrix();
        TomeIndex index = TomeIndex.getIndexByItem(item);
        DynamicTexture tex = cache.getTexture(cooldownPixels, index);
        IVertexBuilder builder = buffer.getBuffer(RenderType.getEntitySolid(Minecraft.getInstance().getTextureManager().getDynamicTextureLocation(index.getString() + "_tome_" + cooldownPixels, tex)));
        ms.push();
        buildItem(tex, builder, 0.125f, combinedLight, matrix4f);
        ms.pop();
    }

    public void buildItem(DynamicTexture texture, IVertexBuilder builder, float size, int combinedLightIn, Matrix4f matrix)
    {
        for(int y = 0; y < texture.getTextureData().getHeight(); y++)
        {
            int fromX = -1;
            int toX = -1;

            for(int x = 0; x < texture.getTextureData().getWidth(); x++)
            {
                if(x == (texture.getTextureData().getWidth()-1))
                {
                    if(fromX != -1)
                    {
                        float uAdd = 1f / texture.getTextureData().getWidth();
                        float vAdd = 1f / texture.getTextureData().getHeight();
                        beginAndEnd(matrix, fromX, toX, y, builder, size, uAdd * x, vAdd * y, uAdd, vAdd, combinedLightIn);
                        fromX = -1;
                        toX = -1;
                    }
                }
                else if(NativeImage.getAlpha(texture.getTextureData().getPixelRGBA(x, y)) > 100)
                {
                    if(fromX == -1)
                    {
                        fromX = x;
                    }
                    else
                    {
                        toX = x;
                    }
                }
                else
                {
                    if(fromX != -1)
                    {
                        float uAdd = 1f / texture.getTextureData().getWidth();
                        float vAdd = 1f / texture.getTextureData().getHeight();
                        beginAndEnd(matrix, fromX, toX, y, builder, size, uAdd * x, vAdd * y, uAdd, vAdd, combinedLightIn);
                        fromX = -1;
                        toX = -1;
                    }
                }
            }
        }
    }

    public void beginAndEnd(Matrix4f matrix, int fromX, int toX, int y, IVertexBuilder builder, float size, float uStart, float vStart, float uAdd, float vAdd, int combinedLightIn)
    {
        float minX = fromX * size;
        float minY = 0;
        float minZ = y * size;

        float maxX = toX * size;
        float maxY = size;
        float maxZ = y * size + size;

        //Left right
        Vector3f n1 = Vector3f.XN;
        builder.pos(matrix, minX, maxY, minZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n1.getX(), n1.getY(), n1.getZ()).endVertex();
        builder.pos(matrix, minX, maxY, maxZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n1.getX(), n1.getY(), n1.getZ()).endVertex();
        builder.pos(matrix, minX, minY, maxZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n1.getX(), n1.getY(), n1.getZ()).endVertex();
        builder.pos(matrix, minX, minY, minZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n1.getX(), n1.getY(), n1.getZ()).endVertex();

        Vector3f n2 = Vector3f.XP;
        builder.pos(matrix, maxX, maxY, minZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n2.getX(), n2.getY(), n2.getZ()).endVertex();
        builder.pos(matrix, maxX, maxY, maxZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n2.getX(), n2.getY(), n2.getZ()).endVertex();
        builder.pos(matrix, maxX, minY, maxZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n2.getX(), n2.getY(), n2.getZ()).endVertex();
        builder.pos(matrix, maxX, minY, minZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n2.getX(), n2.getY(), n2.getZ()).endVertex();

        //Up down
        Vector3f n3 = Vector3f.YN;
        builder.pos(matrix, minX, minY, minZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n3.getX(), n3.getY(), n3.getZ()).endVertex();
        builder.pos(matrix, maxX, minY, minZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n3.getX(), n3.getY(), n3.getZ()).endVertex();
        builder.pos(matrix, maxX, minY, maxZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n3.getX(), n3.getY(), n3.getZ()).endVertex();
        builder.pos(matrix, minX, minY, maxZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n3.getX(), n3.getY(), n3.getZ()).endVertex();

        Vector3f n4 = Vector3f.YP;
        builder.pos(matrix, minX, maxY, minZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n4.getX(), n4.getY(), n4.getZ()).endVertex();
        builder.pos(matrix, maxX, maxY, minZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n4.getX(), n4.getY(), n4.getZ()).endVertex();
        builder.pos(matrix, maxX, maxY, maxZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n4.getX(), n4.getY(), n4.getZ()).endVertex();
        builder.pos(matrix, minX, maxY, maxZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n4.getX(), n4.getY(), n4.getZ()).endVertex();

        //Other left right
        Vector3f n5 = Vector3f.ZN;
        builder.pos(matrix, minX, minY, minZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n5.getX(), n5.getY(), n5.getZ()).endVertex();
        builder.pos(matrix, minX, maxY, minZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n5.getX(), n5.getY(), n5.getZ()).endVertex();
        builder.pos(matrix, maxX, maxY, minZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n5.getX(), n5.getY(), n5.getZ()).endVertex();
        builder.pos(matrix, maxX, minY, minZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n5.getX(), n5.getY(), n5.getZ()).endVertex();

        Vector3f n6 = Vector3f.ZP;
        builder.pos(matrix, minX, minY, maxZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n6.getX(), n6.getY(), n6.getZ()).endVertex();
        builder.pos(matrix, minX, maxY, maxZ).color(255, 255, 255, 255).tex(uStart, vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n6.getX(), n6.getY(), n6.getZ()).endVertex();
        builder.pos(matrix, maxX, maxY, maxZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n6.getX(), n6.getY(), n6.getZ()).endVertex();
        builder.pos(matrix, maxX, minY, maxZ).color(255, 255, 255, 255).tex(uStart + uAdd * (toX - fromX), vStart).overlay(OverlayTexture.NO_OVERLAY).lightmap(combinedLightIn).normal(n6.getX(), n6.getY(), n6.getZ()).endVertex();

    }

}
