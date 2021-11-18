package com.vulp.tomes.items;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.entities.ai.MindBendFollowGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.world.World;

public class DebugItem extends HiddenDescriptorItem {

    public DebugItem(Properties properties) {
        super(properties);
    }

    public void debugEntity(World world, Entity entity, ItemStack stack) {
        if (entity instanceof MobEntity) {
            Tomes.LOGGER.debug("NBT: " + (entity.getPersistentData().hasUniqueId("PlayerFollowingUUID") ? entity.getPersistentData().getUniqueId("PlayerFollowingUUID") : "NULL") + " | MindBendFollowGoal: " + (((MobEntity) entity).goalSelector.goals.stream().anyMatch(prioritizedGoal -> prioritizedGoal.getGoal() instanceof MindBendFollowGoal) ? "Exists!" : "NULL"));
        } else {
            Tomes.LOGGER.debug("Use on MobEntity!");
        }
    }

}
