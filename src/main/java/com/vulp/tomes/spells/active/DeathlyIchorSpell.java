package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.entities.projectile.DeathlyIchorEntity;
import com.vulp.tomes.entities.projectile.WitheringStenchEntity;
import com.vulp.tomes.init.ParticleInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerDeathlyIchorParticleMessage;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.network.PacketDistributor;

public class DeathlyIchorSpell extends ActiveSpell {

    public DeathlyIchorSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        super(rarity, isActive, isRare, enabled);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.deathly_ichor_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
                DeathlyIchorEntity proj = new DeathlyIchorEntity(worldIn, playerIn);
                double d0 = playerIn.getLookVec().getX();
                double d1 = playerIn.getLookVec().getY();
                double d2 = playerIn.getLookVec().getZ();
                proj.shoot(d0, d1, d2, 0.75F, 1.0F);
                worldIn.addEntity(proj);
            }
        return true;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.deathly_ichor_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }
}
