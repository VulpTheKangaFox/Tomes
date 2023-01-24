package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.init.EffectInit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

public class MetamorphosisSpell extends ActiveSpell {

    public MetamorphosisSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        super(rarity, isActive, isRare, enabled);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.metamorphosis_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!playerIn.isPotionActive(EffectInit.winged)) {
            playerIn.addPotionEffect(new EffectInstance(EffectInit.winged, 2160, 0, false, false, true));
            return true;
        }
        return false;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.metamorphosis_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }
}