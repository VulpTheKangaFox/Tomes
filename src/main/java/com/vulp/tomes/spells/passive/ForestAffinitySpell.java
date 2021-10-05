package com.vulp.tomes.spells.passive;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

public class ForestAffinitySpell extends PassiveSpell {

    private boolean toggle = false;
    private Biome lastBiome;

    public ForestAffinitySpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    void fastTick(World world, Entity entity) {

    }

    @Override
    void slowTick(World world, Entity entity) {
        if (!world.isRemote) {
            if (entity instanceof LivingEntity) {
                if (this.toggle) {
                    LivingEntity user = (LivingEntity) entity;
                    user.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 120, 0, true, false));
                    user.addPotionEffect(new EffectInstance(Effects.REGENERATION, 120, 0, true, false));
                }
                Biome currentBiome = world.getBiome(entity.getPosition());
                if (this.lastBiome != null && this.lastBiome != currentBiome) {
                    ResourceLocation key = currentBiome.getRegistryName();
                    this.toggle = key != null && BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, key), BiomeDictionary.Type.FOREST);
                }
                this.lastBiome = currentBiome;
            }
        }
    }

    @Override
    public boolean isTickable() {
        return true;
    }
}
