package com.vulp.tomes.spells.passive;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.init.TagInit;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NurturingRootsSpell extends PassiveSpell {

    private int lastFoodValue = -1;
    private boolean toggle = true;
    private int grassTime = 0;

    public NurturingRootsSpell(Enchantment.Rarity rarity, boolean isActive, boolean isRare) {
        super(rarity, isActive, isRare);
    }

    @Override
    void fastTick(World world, Entity entity) {
        if (!world.isRemote) {
            if (entity instanceof PlayerEntity) {
                if (entity.isOnGround() && isOnGrass(entity.getPosition(), world)) {
                    this.grassTime = 15;
                } else {
                    this.grassTime--;
                }
                FoodStats stats = ((PlayerEntity) entity).getFoodStats();
                int currentLevel = stats.getFoodLevel();
                if (this.grassTime > 0 && this.lastFoodValue != -1 && stats.getFoodLevel() < this.lastFoodValue) {
                    if (this.toggle) {
                        stats.setFoodLevel(this.lastFoodValue);
                    }
                    this.toggle = !this.toggle;
                }
                this.lastFoodValue = currentLevel;
            }
        }
    }

    private boolean isOnGrass(BlockPos pos, World world) {
        Block block = world.getBlockState(pos.down()).getBlock();
        return block.isIn(TagInit.GRASS) || block.isIn(BlockTags.NYLIUM);
    }

    @Override
    void slowTick(World world, Entity entity) {

    }

    @Override
    public boolean isTickable() {
        return true;
    }
}
