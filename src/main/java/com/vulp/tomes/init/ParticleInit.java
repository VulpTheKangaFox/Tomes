package com.vulp.tomes.init;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vulp.tomes.particles.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleInit {

    @OnlyIn(Dist.CLIENT)
    public static class ParticleTypes {
        public static final IParticleRenderType RENDERER_LIT_NOCULL = new IParticleRenderType() {
            public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager) {
                RenderSystem.disableBlend();
                RenderSystem.disableCull();
                RenderSystem.depthMask(true);
                textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
                bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            }

            public void finishRender(Tessellator tesselator) {
                tesselator.draw();
            }

            public String toString() {
                return "RENDERER_LIT_NOCULL";
            }
        };

        public static final IParticleRenderType RENDERER_TRANSPARENT_NOCULL = new IParticleRenderType() {
            public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager) {
                RenderSystem.depthMask(true);
                RenderSystem.disableCull();
                textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.alphaFunc(516, 0.003921569F);
                bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            }

            public void finishRender(Tessellator tesselator) {
                tesselator.draw();
            }

            public String toString() {
                return "RENDERER_TRANSPARENT_NOCULL";
            }
        };
    }

    public static final BasicParticleType spirit_flame = new BasicParticleType(false);
    public static final BasicParticleType withering_stench = new BasicParticleType(false);
    public static final BasicParticleType wind_deflect = new BasicParticleType(true);
    public static final BasicParticleType web_net = new BasicParticleType(true);
    public static final BasicParticleType hex = new BasicParticleType(true);
    public static final BasicParticleType wild_wolf_despawn = new BasicParticleType(false);
    public static final BasicParticleType spectral_steed_despawn = new BasicParticleType(false);
    public static final BasicParticleType living_wisp = new BasicParticleType(false);

    @OnlyIn(Dist.CLIENT)
    public static void registerFactories() {
        ParticleManager particles = Minecraft.getInstance().particles;
        particles.registerFactory(spirit_flame, SpiritFlameParticle.Factory::new);
        particles.registerFactory(withering_stench, WitheringStenchParticle.Factory::new);
        particles.registerFactory(wind_deflect, WindDeflectParticle.Factory::new);
        particles.registerFactory(web_net, WebNetParticle.Factory::new);
        particles.registerFactory(hex, HexParticle.Factory::new);
        particles.registerFactory(wild_wolf_despawn, GlowDustParticle.WildWolfFactory::new);
        particles.registerFactory(spectral_steed_despawn, GlowDustParticle.SpectralSteedFactory::new);
        particles.registerFactory(living_wisp, LivingWispParticle.Factory::new);
    }

}
