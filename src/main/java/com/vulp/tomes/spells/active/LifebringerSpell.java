package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.init.ParticleInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Random;

public class LifebringerSpell extends ActiveSpell {

    private int timer = 0;
    private Vector3d playerPos;

    public LifebringerSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare, ForgeConfigSpec.ConfigValue<Boolean> enabled) {
        super(rarity, isActive, isRare, enabled);
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

        } else {
            timer = 30;
            this.playerPos = playerIn.getPositionVec();
        }
        return true;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.lifebringer_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return true;
    }

    @Override
    public void tick(World world, Entity entity) {
        if (world.isRemote) {
            if (this.timer > 0 && this.timer % 5 == 0) {
                for (int i = 0; i < 8; i++) {
                    Random rand = new Random();
                    float j = (rand.nextFloat() - 0.5F) * 0.5F;
                    float k = (rand.nextFloat() - 0.5F) * 0.5F;
                    float l = (rand.nextFloat() - 0.5F) * 0.5F;
                    world.addParticle(ParticleInit.living_wisp, playerPos.x + (rand.nextFloat() * 7.0F) - 3.5F, playerPos.y + (rand.nextFloat() * 7.0F) - 3.5F, playerPos.z + (rand.nextFloat() * 7.0F) - 3.5F, j, k, l);
                }
            }
            this.timer--;
        }
    }

}