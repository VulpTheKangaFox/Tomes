package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class LifebringerSpell extends ActiveSpell {

    public LifebringerSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.lifebringer_cost.get();
    }

    // TODO: Hearts show up even when animal is on cooldown!
    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        BlockPos playerPos = playerIn.getPosition();
        if (!worldIn.isRemote) {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 7; z++) {
                    for (int y = 0; y < 7; y++) {
                        BlockPos pos = playerPos.add(x - 3, y - 3, z - 3);
                        BlockState state = worldIn.getBlockState(pos);
                        if (state.getBlock() instanceof IGrowable) {
                            ((IGrowable) state.getBlock()).grow((ServerWorld) worldIn, worldIn.rand, pos, state);
                        }
                    }
                }
            }
            List<AnimalEntity> list = worldIn.getEntitiesWithinAABB(AnimalEntity.class, new AxisAlignedBB(playerPos.add(-3, -3, -3), playerPos.add(3, 3, 3)), (animalEntity -> !animalEntity.isChild()));
            for (AnimalEntity animal : list) {
                if (animal.canFallInLove()) {
                    animal.setInLove(playerIn);
                }
            }

        }
        return true;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.lifebringer_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }
}