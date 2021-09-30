package com.vulp.tomes.init;

import com.vulp.tomes.particles.SpiritFlameParticle;
import com.vulp.tomes.particles.WitheringStenchParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleInit {

    public static final BasicParticleType spirit_flame = new BasicParticleType(false);
    public static final BasicParticleType withering_stench = new BasicParticleType(false);

    @OnlyIn(Dist.CLIENT)
    public static void registerFactories() {
        ParticleManager particles = Minecraft.getInstance().particles;
        particles.registerFactory(spirit_flame, SpiritFlameParticle.Factory::new);
        particles.registerFactory(withering_stench, WitheringStenchParticle.Factory::new);
    }

}
