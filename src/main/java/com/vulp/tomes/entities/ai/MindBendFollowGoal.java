package com.vulp.tomes.entities.ai;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class MindBendFollowGoal extends Goal{

    private final MobEntity mobEntity;
    private LivingEntity player;
    private final World world;
    private final double followSpeed;
    private final PathNavigator navigator;
    private int timeToRecalcPath;
    private final float maxDist;
    private final float minDist;
    private float oldWaterCost;

    public MindBendFollowGoal(MobEntity mobEntity, double speed, float minDist, float maxDist) {
        this.mobEntity = mobEntity;
        this.world = mobEntity.world;
        this.followSpeed = speed;
        this.navigator = mobEntity.getNavigator();
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mobEntity.getNavigator() instanceof GroundPathNavigator) && !(mobEntity.getNavigator() instanceof FlyingPathNavigator)) {
            Tomes.LOGGER.error("Unsupported mob navigator for MindBendFollowGoal! Effect may act odd.");
        }
    }

    @Nullable
    public PlayerEntity getPlayer(World world) {
        CompoundNBT nbt = this.mobEntity.getPersistentData();
        return nbt.hasUniqueId("PlayerFollowingUUID") ? world.getPlayerByUuid(nbt.getUniqueId("PlayerFollowingUUID")) : null;
    }

    public boolean hasMindBent() {
        return this.mobEntity.isPotionActive(EffectInit.mind_bend);
    }

    @Override
    public boolean shouldExecute() {
        if (this.hasMindBent()) {
            LivingEntity livingentity = this.getPlayer(this.world);
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.mobEntity.getDistanceSq(livingentity) < (double) (this.minDist * this.minDist)) {
                return false;
            } else {
                this.player = livingentity;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.hasMindBent()) {
            if (this.navigator.noPath()) {
                return false;
            } else {
                return !(this.mobEntity.getDistanceSq(this.player) <= (double) (this.maxDist * this.maxDist));
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mobEntity.getPathPriority(PathNodeType.WATER);
        this.mobEntity.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    @Override
    public void resetTask() {
        this.player = null;
        this.navigator.clearPath();
        this.mobEntity.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
        CompoundNBT nbt = this.mobEntity.serializeNBT();
        if (nbt.hasUniqueId("PlayerFollowingUUID")) {
            nbt.remove("PlayerFollowingUUID");
        }
    }

    @Override
    public void tick() {
        this.mobEntity.getLookController().setLookPositionWithEntity(this.player, 10.0F, (float)this.mobEntity.getVerticalFaceSpeed());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.mobEntity.getLeashed() && !this.mobEntity.isPassenger()) {
                this.navigator.tryMoveToEntityLiving(this.player, this.followSpeed);
            }
        }
    }

}
