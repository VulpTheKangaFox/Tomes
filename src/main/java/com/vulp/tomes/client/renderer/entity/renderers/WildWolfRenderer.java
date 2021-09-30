package com.vulp.tomes.client.renderer.entity.renderers;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.entities.WildWolfEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class WildWolfRenderer extends WolfRenderer {

    // TODO: Maybe an eye layer for glowy magic eyes?

    private static final ResourceLocation WILD_WOLF_TEXTURES = new ResourceLocation(Tomes.MODID, "textures/entity/wild_wolf.png");

    public WildWolfRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.layerRenderers.remove(this.layerRenderers.size() - 1);
    }

    public ResourceLocation getEntityTexture(WolfEntity entity) {
        return WILD_WOLF_TEXTURES;
    }

    public static class RenderFactory implements IRenderFactory<WildWolfEntity> {
        @Override
        public EntityRenderer<? super WildWolfEntity> createRenderFor(EntityRendererManager manager) {
            return new WildWolfRenderer(manager);
        }
    }

}
