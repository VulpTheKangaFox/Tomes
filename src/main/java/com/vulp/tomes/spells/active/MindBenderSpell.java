package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.entities.ai.MindBendFollowGoal;
import com.vulp.tomes.entities.ai.NullifyAttackableTargetGoal;
import com.vulp.tomes.init.EffectInit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class MindBenderSpell extends ActiveSpell {

    public MindBenderSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        super(rarity, isActive, isRare, enabled);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.mind_bender_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        LivingEntity target = (LivingEntity) this.getTarget();
        if (target instanceof MobEntity) {
            if (!worldIn.isRemote()) {
                boolean effect = target.isPotionActive(EffectInit.mind_bend);
                if (target.getMaxHealth() <= 20 && !effect) {
                    target.getPersistentData().putUniqueId("PlayerFollowingUUID", playerIn.getUniqueID());
                    ((MobEntity) target).goalSelector.addGoal(1, new MindBendFollowGoal((MobEntity) target, 1.0F, 5.0F, 2.0F));
                    ((MobEntity) target).targetSelector.addGoal(0, new NullifyAttackableTargetGoal((MobEntity) target, false));
                    target.addPotionEffect(new EffectInstance(EffectInit.mind_bend, 2400, 0, false, false));
                    return true;
                } else if (effect) {
                    target.removePotionEffect(EffectInit.mind_bend);
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.mind_bender_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }
}
