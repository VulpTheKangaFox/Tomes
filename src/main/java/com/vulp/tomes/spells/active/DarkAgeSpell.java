package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;

public class DarkAgeSpell extends ActiveSpell {

    private static long TIME = -1;

    public DarkAgeSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        super(rarity, isActive, isRare, enabled);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.dark_age_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isNightTime()) {
            TIME = worldIn.getDayTime();
            return true;
        }
        return false;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.dark_age_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return true;
    }

    // TODO: Allow smooth visual on client-side.
    @Override
    public void tick(World world, Entity entity) {
        if (TIME != -1L) {
            if (TIME % 24000L > 13000L) {
                TIME = -1L;
            } else {
                TIME += 80;
                if (!world.isRemote()) {
                    ((ServerWorld) world).setDayTime(TIME);
                } else {
                    ((ClientWorld) world).setDayTime(TIME);
                }
            }
        }
    }

}
