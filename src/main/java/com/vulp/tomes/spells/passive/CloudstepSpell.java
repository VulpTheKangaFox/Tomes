package com.vulp.tomes.spells.passive;

import com.vulp.tomes.init.EffectInit;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

public class CloudstepSpell extends PassiveSpell {

    public CloudstepSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        super(rarity, isActive, isRare, enabled);
    }

    @Override
    void fastTick(World world, Entity entity) {

    }

    @Override
    void slowTick(World world, Entity entity) {
        if (!world.isRemote && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            living.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 320, 0, true, false));
            living.addPotionEffect(new EffectInstance(EffectInit.light_footed, 320, 1, true, false));
            living.addPotionEffect(new EffectInstance(EffectInit.multi_jump, 320, 0, true, false));
        }
    }

    @Override
    public boolean isTickable() {
        return true;
    }

}
