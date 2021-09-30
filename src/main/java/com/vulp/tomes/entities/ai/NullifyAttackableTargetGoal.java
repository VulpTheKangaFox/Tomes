package com.vulp.tomes.entities.ai;

import com.vulp.tomes.init.EffectInit;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

public class NullifyAttackableTargetGoal extends TargetGoal {

    public NullifyAttackableTargetGoal(MobEntity goalOwnerIn, boolean checkSight) {
        super(goalOwnerIn, checkSight, false);
    }

    public boolean hasMindBent() {
        return this.goalOwner.isPotionActive(EffectInit.mind_bend);
    }

    @Override
    public boolean shouldExecute() {
        return this.hasMindBent();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.hasMindBent();
    }

    @Override
    public void tick() {
        this.goalOwner.setAttackTarget(null);
        this.target = null;
        super.tick();
    }

    @Override
    public void startExecuting() {
        this.goalOwner.setAttackTarget(null);
        this.target = null;
        super.startExecuting();
    }

}
