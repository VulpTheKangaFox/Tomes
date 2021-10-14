package com.vulp.tomes.util;

import com.vulp.tomes.init.EffectInit;
import net.minecraft.entity.LivingEntity;

public class HealthHandler {

    private boolean shouldHeal;
    private final LivingEntity entity;
    private boolean setForRemoval = false;

    public HealthHandler(LivingEntity entity) {
        this(entity, true);
    }

    public HealthHandler(LivingEntity entity, boolean boolStart) {
        this.shouldHeal = boolStart;
        this.entity = entity;
    }


    public boolean shouldHeal() {
        return this.shouldHeal;
    }

    public void toggleHeal() {
        this.shouldHeal = !this.shouldHeal;
    }

    public void tick() {
        if (this.entity == null || this.entity.getShouldBeDead() || !this.entity.isPotionActive(EffectInit.leaden_veins)) {
            this.remove();
        }
    }

    public void remove() {
        this.setForRemoval = true;
    }

    public boolean readyToRemove() {
        return this.setForRemoval;
    }

}
