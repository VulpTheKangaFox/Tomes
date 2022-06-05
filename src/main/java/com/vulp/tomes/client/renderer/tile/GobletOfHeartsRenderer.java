package com.vulp.tomes.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.blocks.tile.GobletOfHeartsTileEntity;
import com.vulp.tomes.client.renderer.RenderTypes;
import com.vulp.tomes.init.BlockInit;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;

public class GobletOfHeartsRenderer extends TileEntityRenderer<GobletOfHeartsTileEntity> {

    protected static int ticker = 0;
    public static final ResourceLocation texture = TomesRegistry.location("entity/goblet_of_hearts_effects");
    private static final RenderMaterial material = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, texture);
    private final GobletOfHeartsEffectsModel model = new GobletOfHeartsEffectsModel();

    public GobletOfHeartsRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(GobletOfHeartsTileEntity goblet, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        NonNullList<ItemStack> inv = goblet.getInventory();
        int items = 0;
        for (ItemStack itemStack : inv) {
            if (itemStack.isEmpty()) {
                break;
            } else {
                items++;
            }
        }
        matrixStackIn.push();
        BlockState state = BlockInit.goblet_of_hearts.getDefaultState();
        Minecraft mc = Minecraft.getInstance();
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderTypeLookup.func_239220_a_(state, false));
        matrixStackIn.pop();
        matrixStackIn.push();
        matrixStackIn.translate(0.5F, -0.5F, 0.5F);
        this.model.render(matrixStackIn, material.getBuffer(bufferIn, this.model::getRenderType), 15728640, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.translate(-0.5F, 0.5F, -0.5F);
        float[] angles = new float[inv.size()];

        float anglePer = 360F / items;
        float totalAngle = 0F;
        for (int i = 0; i < angles.length; i++) {
            angles[i] = totalAngle += anglePer;
        }

        double time = partialTicks + ticker;
        for (int i = 0; i < inv.size(); i++) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5F, 0.75F, 0.5F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(angles[i] + (float) time));
            matrixStackIn.translate(0.5F, 0F, 0.0F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90F));
            matrixStackIn.translate(0D, 0F/*0.075 * Math.sin((time + i * 10) / 5D)*/, 0F);
            matrixStackIn.scale(0.75F, 0.75F, 0.75F);
            ItemStack stack = inv.get(i);
            if (!stack.isEmpty()) {
                mc.getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
            }
            matrixStackIn.pop();
        }
        matrixStackIn.pop();
    }



    public static void renderReadyGlow(MatrixStack matrixStackIn, IVertexBuilder bufferIn) {
        renderReadyGlow(matrixStackIn, bufferIn, 0.8125F, 0.75F, 0.177F);
    }

    public static void renderReadyGlow(MatrixStack matrixStackIn, IVertexBuilder bufferIn, float yOffset, float height, float beamRadius) {
        float i = yOffset + height;
        matrixStackIn.push();
        matrixStackIn.translate(0.5D, 0.0D, 0.5D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(45.0F));
        renderPart(matrixStackIn, bufferIn, 1.0F, yOffset, i, 0.0F, beamRadius, beamRadius, 0.0F, -beamRadius, 0.0F, 0.0F, -beamRadius);
        matrixStackIn.pop();
    }

    private static void renderPart(MatrixStack matrixStackIn, IVertexBuilder bufferIn, float alpha, float yMin, float yMax, float a, float b, float c, float d, float e, float f, float g, float h) {
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        float[] color1 = new float[]{0.0F, 1.0F, 1.0F};
        float[] color2 = new float[]{1.0F, 1.0F, 1.0F};

        addVertex(matrix4f, bufferIn, color1[0], color1[1], color1[2], 0.0F, yMax, a, b + 0.3F);
        addVertex(matrix4f, bufferIn, color2[0], color2[1], color2[2], alpha, yMin, a, b);
        addVertex(matrix4f, bufferIn, color2[0], color2[1], color2[2], alpha, yMin, c, d);
        addVertex(matrix4f, bufferIn, color1[0], color1[1], color1[2], 0.0F, yMax, c + 0.3F, d);

        addVertex(matrix4f, bufferIn, color1[0], color1[1], color1[2], 0.0F, yMax, g, h - 0.3F);
        addVertex(matrix4f, bufferIn, color2[0], color2[1], color2[2], alpha, yMin, g, h);
        addVertex(matrix4f, bufferIn, color2[0], color2[1], color2[2], alpha, yMin, e, f);
        addVertex(matrix4f, bufferIn, color1[0], color1[1], color1[2], 0.0F, yMax, e - 0.3F, f);

        addVertex(matrix4f, bufferIn, color1[0], color1[1], color1[2], 0.0F, yMax, c + 0.3F, d);
        addVertex(matrix4f, bufferIn, color2[0], color2[1], color2[2], alpha, yMin, c, d);
        addVertex(matrix4f, bufferIn, color2[0], color2[1], color2[2], alpha, yMin, g, h);
        addVertex(matrix4f, bufferIn, color1[0], color1[1], color1[2], 0.0F, yMax, g, h - 0.3F);

        addVertex(matrix4f, bufferIn, color1[0], color1[1], color1[2], 0.0F, yMax, e - 0.3F, a);
        addVertex(matrix4f, bufferIn, color2[0], color2[1], color2[2], alpha, yMin, e, a);
        addVertex(matrix4f, bufferIn, color2[0], color2[1], color2[2], alpha, yMin, f, b);
        addVertex(matrix4f, bufferIn, color1[0], color1[1], color1[2], 0.0F, yMax, f, b + 0.3F);

        addVertex(matrix4f, bufferIn, 1.0F, 1.0F, 1.0F, 1.0F, yMin, a, b);
        addVertex(matrix4f, bufferIn, 1.0F, 1.0F, 1.0F, 1.0F, yMin, e, f);
        addVertex(matrix4f, bufferIn, 1.0F, 1.0F, 1.0F, 1.0F, yMin, g, h);
        addVertex(matrix4f, bufferIn, 1.0F, 1.0F, 1.0F, 1.0F, yMin, c, d);
    }

    private static void addVertex(Matrix4f matrixPos, IVertexBuilder bufferIn, float red, float green, float blue, float alpha, float y, float x, float z) {
        bufferIn.pos(matrixPos, x, y, z).color(red, green, blue, alpha).endVertex();
    }

    public static void tick() {
        ticker++;
    }

    public static class GobletOfHeartsEffectsModel extends Model {
        private final ModelRenderer gems;
        public float flareAlpha = 0.0F;

        public GobletOfHeartsEffectsModel() {
            super(RenderTypes::getSpectralBlock);
            textureWidth = 32;
            textureHeight = 32;

            gems = new ModelRenderer(this);
            gems.setRotationPoint(0.0F, 16.0F, 0.0F);
            gems.setTextureOffset(0, 0)
                    .addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F, false);

        }

        @Override
        public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            gems.rotateAngleX = (float)Math.PI;
            matrixStack.scale(1.005F, 1F, 1.005F);
            gems.render(matrixStack, buffer, packedLight, packedOverlay);
        }

    }

}
