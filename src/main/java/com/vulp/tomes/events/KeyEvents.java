package com.vulp.tomes.events;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.effects.MultiJumpEffect;
import com.vulp.tomes.init.EffectInit;
import com.vulp.tomes.network.TomesPacketHandler;
import com.vulp.tomes.network.messages.ClientJumpPressedMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value= Dist.CLIENT)
public class KeyEvents {

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        // Spacebar pressed:
        if (event.getKey() == 32 && event.getAction() == 1) {
            ClientPlayerEntity playerEntity = Minecraft.getInstance().player;
            if (playerEntity != null && playerEntity.isPotionActive(EffectInit.multi_jump) && !playerEntity.isOnGround() && !playerEntity.abilities.isFlying && (!playerEntity.isCreative() || playerEntity.flyToggleTimer == 0) && !playerEntity.isSwimming()) {
                MultiJumpEffect.doAirJump(playerEntity);
                TomesPacketHandler.instance.sendToServer(new ClientJumpPressedMessage(playerEntity.getUniqueID()));
            }
        }
    }

}
