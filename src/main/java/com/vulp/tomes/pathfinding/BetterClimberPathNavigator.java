package com.vulp.tomes.pathfinding;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.world.World;

public class BetterClimberPathNavigator extends ClimberPathNavigator {

    public BetterClimberPathNavigator(MobEntity entityLivingIn, World worldIn) {
        super(entityLivingIn, worldIn);
    }

    @Override
    public void clearPath() {
        super.clearPath();
    }

    public void clearPathForSitting() {
        this.targetPosition = null;
        super.clearPath();
    }
}
