package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class EverchangingSkiesSpell extends ActiveSpell {

    public EverchangingSkiesSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.everchanging_skies_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            ServerWorld serverWorld = (ServerWorld) worldIn;
            Random rand = new Random();
            int weatherTime = rand.nextInt(4000) + 4000;
            if (serverWorld.isRaining()) {
                serverWorld.setWeather(weatherTime, 0, false, false);
            } else {
                if (rand.nextInt(3) == 0) {
                    serverWorld.setWeather(0, weatherTime, true, false);
                } else {
                    serverWorld.setWeather(0, weatherTime, true, true);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.everchanging_skies_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }

}
