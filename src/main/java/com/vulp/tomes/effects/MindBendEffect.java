package com.vulp.tomes.effects;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.entities.ai.MindBendFollowGoal;
import com.vulp.tomes.entities.ai.NullifyAttackableTargetGoal;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ServerMindBendMessage;
import com.vulp.tomes.spells.active.MindBenderSpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.network.PacketDistributor;

public class MindBendEffect extends TomeEffect {

    public MindBendEffect() {
        super(EffectType.HARMFUL, 9830455, false);
    }

    @Override
    public void applyAttributesModifiersToEntity(LivingEntity entity, AttributeModifierManager attributeMapIn, int amplifier) {
        if (!entity.world.isRemote) {
            TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerMindBendMessage(entity.getEntityId(), true));
            CompoundNBT nbt = entity.getPersistentData();
            nbt.putBoolean("HexParticle", true);
            if (!nbt.hasUniqueId("PlayerFollowingUUID")) {
                Tomes.LOGGER.error("MindBendEffect applied through unusual means or commands. Effect will likely not cause anything to happen.");
            }
        }
        super.applyAttributesModifiersToEntity(entity, attributeMapIn, amplifier);
    }

    @Override
    public void removeAttributesModifiersFromEntity(LivingEntity entity, AttributeModifierManager attributeMapIn, int amplifier) {
        if (!entity.world.isRemote) {
            TomesPacketHandler.instance.send(PacketDistributor.ALL.noArg(), new ServerMindBendMessage(entity.getEntityId(), false));
            CompoundNBT nbt = entity.getPersistentData();
            nbt.remove("PlayerFollowingUUID");
            nbt.remove("HexParticle");
            if (entity instanceof MobEntity) {
                ((MobEntity) entity).goalSelector.removeGoal(new MindBendFollowGoal((MobEntity) entity, 1.0F, 5.0F, 2.0F));
                ((MobEntity) entity).targetSelector.removeGoal(new NullifyAttackableTargetGoal((MobEntity) entity, false));
            }
        }
        super.removeAttributesModifiersFromEntity(entity, attributeMapIn, amplifier);
    }

    @Override
    void potionTick(LivingEntity entity, int amplifier) {

    }

    @Override
    boolean readyToTick(int duration, int amplifier) {
        return false;
    }

}
