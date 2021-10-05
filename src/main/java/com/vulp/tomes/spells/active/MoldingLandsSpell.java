package com.vulp.tomes.spells.active;

import com.vulp.tomes.config.TomesConfig;
import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class MoldingLandsSpell extends ActiveSpell {

    public MoldingLandsSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    public int getSpellCost() {
        return TomesConfig.molding_lands_cost.get();
    }

    @Override
    public boolean onCast(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            int r = 6;
            Random rand = new Random();
            BlockPos playerPos = playerIn.getPosition();
            boolean mush = rand.nextBoolean();
            final boolean[] flag = {false};
            for (int tx = -r; tx < r + 1; tx++) {
                for (int ty = -r; ty < r + 1; ty++) {
                    for (int tz = -r; tz < r + 1; tz++) {
                        if (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)) <= r - 2) {
                            BlockPos pos = new BlockPos(tx + playerPos.getX(), ty + playerPos.getY(), tz + playerPos.getZ());
                            if (worldIn.getBlockState(pos).getBlock() instanceof GrassBlock) {
                                worldIn.setBlockState(pos, Blocks.MYCELIUM.getDefaultState());
                                if (worldIn.getBlockState(pos.up()).getBlock() instanceof AirBlock) {
                                    if (rand.nextInt(20) == 0) {
                                        if (rand.nextBoolean()) {
                                            worldIn.setBlockState(pos.up(), Blocks.RED_MUSHROOM.getDefaultState());
                                        } else {
                                            worldIn.setBlockState(pos.up(), Blocks.BROWN_MUSHROOM.getDefaultState());
                                        }
                                    }
                                }
                            } else if (worldIn.getBlockState(pos).getBlock().isIn(BlockTags.LOGS)) {
                                worldIn.setBlockState(pos, Blocks.MUSHROOM_STEM.getDefaultState());
                            } else if (worldIn.getBlockState(pos).getBlock() instanceof LeavesBlock) {
                                if (mush) {
                                    worldIn.setBlockState(pos, Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState());
                                } else {
                                    worldIn.setBlockState(pos, Blocks.RED_MUSHROOM_BLOCK.getDefaultState());
                                }
                            } else {
                                continue;
                            }
                            flag[0] = true;
                        }
                    }
                }
            }
            worldIn.getEntitiesWithinAABB(CowEntity.class, new AxisAlignedBB(playerPos.add(-3, -3, -3), playerPos.add(3, 3, 3)), e -> !(e instanceof MooshroomEntity)).forEach(e -> {
                EntityType.MOOSHROOM.spawn((ServerWorld) worldIn, e.serializeNBT(), e.getCustomName(), playerIn, e.getPosition(), SpawnReason.CONVERSION, false, false);
                e.remove();
                flag[0] = true;
            });
            return flag[0];
        }
        return false;
    }

    @Override
    public int getCooldown() {
        return TomesConfig.molding_lands_cooldown.get();
    }

    @Override
    public boolean canTick() {
        return false;
    }

    @Override
    public void tick(World world, Entity entity) {

    }

}
