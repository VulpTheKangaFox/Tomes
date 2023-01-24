package com.vulp.tomes.client.renderer.entity.renderers;

import com.vulp.tomes.entities.projectile.DeathlyIchorEntity;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class DeathlyIchorRenderer<T extends DeathlyIchorEntity> extends EntityRenderer<T> {

    public DeathlyIchorRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return null;
    }

    @Override
    public boolean shouldRender(T livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        return true;
    }

    public static class RenderFactory implements IRenderFactory<DeathlyIchorEntity> {
        @Override
        public EntityRenderer<? super DeathlyIchorEntity> createRenderFor(EntityRendererManager manager) {
            return new DeathlyIchorRenderer<>(manager);
        }
    }

}
