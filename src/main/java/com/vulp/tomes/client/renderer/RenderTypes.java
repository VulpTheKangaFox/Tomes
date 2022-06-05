package com.vulp.tomes.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vulp.tomes.TomesRegistry;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTypes extends RenderType {

    public RenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType getSpectralBlock(ResourceLocation locationIn) {
        return getSpectral(locationIn, false);
    }

    public static RenderType getLightFlare() {
        return makeType("light_flare", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, RenderType.State.getBuilder().writeMask(COLOR_DEPTH_WRITE).transparency(RenderState.TRANSLUCENT_TRANSPARENCY).cull(CULL_DISABLED).shadeModel(SHADE_ENABLED).build(false));
    }

    public static RenderType getFlat() {
        return makeType("flat", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, RenderType.State.getBuilder().build(true));
    }

    public static RenderType getTest() {
        return makeType("test", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, RenderType.State.getBuilder().transparency(RenderState.TRANSLUCENT_TRANSPARENCY).texture(new RenderState.TextureState(TomesRegistry.location("textures/test.png"), false, false)).texturing(new RenderState.TexturingState("test", () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.translatef(0.5F, 0.5F, 0.0F);
            RenderSystem.scalef(0.5F, 0.5F, 1.0F);
            RenderSystem.translatef(0.0F, -2.0F * ((float)(Util.milliTime() % 4000L) / 4000.0F), 0.0F);
            RenderSystem.scalef(8.0F, 8.0F, 1.0F);
            RenderSystem.mulTextureByProjModelView();
            RenderSystem.matrixMode(5888);
            RenderSystem.setupEndPortalTexGen();
        }, () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
            RenderSystem.clearTexGen();
        })).fog(RenderState.NO_FOG).cull(CULL_ENABLED).build(false));
    }

    public static RenderType getTestOutline() {
        return makeType("test_outline", DefaultVertexFormats.POSITION_COLOR_TEX, 7, 256, false, true, RenderType.State.getBuilder().transparency(RenderState.TRANSLUCENT_TRANSPARENCY).texture(new RenderState.TextureState(TomesRegistry.location("textures/test.png"), false, false)).texturing(new RenderState.TexturingState("test", () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.translatef(0.5F, 0.5F, 0.0F);
            RenderSystem.scalef(0.5F, 0.5F, 1.0F);
            RenderSystem.translatef(0.0F, -2.0F * ((float)(Util.milliTime() % 3500L) / 3500L), 0.0F);
            RenderSystem.scalef(8.0F, 8.0F, 1.0F);
            RenderSystem.mulTextureByProjModelView();
            RenderSystem.matrixMode(5888);
            RenderSystem.setupEndPortalTexGen();
        }, () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
            RenderSystem.clearTexGen();
        })).fog(RenderState.NO_FOG).cull(CULL_ENABLED).target(OUTLINE_TARGET).build(OutlineState.IS_OUTLINE));
    }

    public static RenderType getSpectral(ResourceLocation locationIn, boolean outlineIn) {
        RenderState.TextureState renderstate$texturestate = new RenderState.TextureState(locationIn, false, false);
        return makeType("spectral", DefaultVertexFormats.ENTITY, 7, 256, false, true, RenderType.State.getBuilder().texture(renderstate$texturestate).transparency(TRANSLUCENT_TRANSPARENCY).writeMask(COLOR_DEPTH_WRITE).fog(BLACK_FOG).alpha(RenderState.DEFAULT_ALPHA).cull(CULL_DISABLED).overlay(OVERLAY_ENABLED).build(outlineIn));
    }

}
