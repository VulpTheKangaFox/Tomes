package com.vulp.tomes.events;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent
    public static void onRenderHandEvent(RenderHandEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof TomeItem && stack.isEnchanted()) {
            Minecraft minecraft = Minecraft.getInstance();
            TomeItem tome = (TomeItem) stack.getItem();
            ItemStack newStack = stack.copy();
            if (tome == ItemInit.archaic_tome) {
                newStack = new ItemStack(ItemInit.archaic_tome_open);
            } else if (tome == ItemInit.living_tome) {
                newStack = new ItemStack(ItemInit.living_tome_open);
            } else if (tome == ItemInit.cursed_tome) {
                newStack = new ItemStack(ItemInit.cursed_tome_open);
            }
            minecraft.getFirstPersonRenderer().renderItemInFirstPerson(minecraft.player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), newStack, event.getEquipProgress(), event.getMatrixStack(), event.getBuffers(), event.getLight());
            event.setCanceled(true);
        }
    }


}
