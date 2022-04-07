package com.vulp.tomes.events;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid= Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class RenderEvents {

    private static boolean HOOK_TOGGLE = false;

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

    @SubscribeEvent
    public static void onRenderTooltipEvent(RenderTooltipEvent.Pre event) {
        // Latch helps make sure that calling func_243308_b below doesn't cause an infinite loop of forge hook events.
        if (HOOK_TOGGLE) {
            HOOK_TOGGLE = false;
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        if (player != null) {
            PlayerInventory inventory = player.inventory;
            boolean hasTome = inventory != null && Arrays.stream(new ItemStack[]{inventory.getCurrentItem(), inventory.offHandInventory.get(0)}).anyMatch(item -> item.getItem() instanceof TomeItem && EnchantmentHelper.getEnchantments(item).containsKey(EnchantmentInit.linguist));
            if (TomesConfig.linguist_enabled.get() && hasTome) {
                Screen screen = minecraft.currentScreen;
                if (screen instanceof ContainerScreen && ((ContainerScreen<?>) screen).getContainer() instanceof EnchantmentContainer) {
                    ContainerScreen<?> containerScreen = (ContainerScreen<?>) screen;
                    EnchantmentContainer container = (EnchantmentContainer) containerScreen.getContainer();
                    EnchantClueHolder clueHolder = EnchantClueHolder.decodeClues(container.enchantClue);
                    for (int j = 0; j < 3; ++j) {
                        List<Pair<Enchantment, Integer>> clueList = clueHolder.getData(j);
                        List<ITextComponent> list = Lists.newArrayList();
                        int k = 0;
                        int i1 = j + 1;
                        ItemStack stack = container.tableInventory.getStackInSlot(0);
                        if (isPointInRegion(60, 14 + 19 * j, 108, 17, event.getX(), event.getY(), containerScreen.getGuiLeft(), containerScreen.getGuiTop()) && clueList.size() > 0 && !stack.isEmpty() && !stack.isEnchanted() && stack.isEnchantable()) {
                            for (Pair<Enchantment, Integer> clue : clueList) {
                                if (clue.getFirst() != null) {
                                    list.add(getDisplayName(clue.getFirst(), clue.getSecond()));
                                } else {
                                    list.add(new StringTextComponent(". . .").mergeStyle(TextFormatting.AQUA));
                                }
                            }
                            if (clueList.isEmpty()) {
                                list.add(new StringTextComponent(""));
                                list.add(new TranslationTextComponent("forge.container.enchant.limitedEnchantability").mergeStyle(TextFormatting.RED));
                            } else if (!player.abilities.isCreativeMode) {
                                list.add(StringTextComponent.EMPTY);
                                if (player.experienceLevel < k) {
                                    list.add((new TranslationTextComponent("container.enchant.level.requirement", (container).enchantLevels[j])).mergeStyle(TextFormatting.RED));
                                } else {
                                    IFormattableTextComponent iformattabletextcomponent;
                                    if (i1 == 1) {
                                        iformattabletextcomponent = new TranslationTextComponent("container.enchant.lapis.one");
                                    } else {
                                        iformattabletextcomponent = new TranslationTextComponent("container.enchant.lapis.many", i1);
                                    }

                                    list.add(iformattabletextcomponent.mergeStyle(container.getLapisAmount() >= i1 ? TextFormatting.GRAY : TextFormatting.RED));
                                    IFormattableTextComponent iformattabletextcomponent1;
                                    if (i1 == 1) {
                                        iformattabletextcomponent1 = new TranslationTextComponent("container.enchant.level.one");
                                    } else {
                                        iformattabletextcomponent1 = new TranslationTextComponent("container.enchant.level.many", i1);
                                    }

                                    list.add(iformattabletextcomponent1.mergeStyle(TextFormatting.GRAY));
                                }
                            }
                            HOOK_TOGGLE = true;
                            containerScreen.func_243308_b(event.getMatrixStack(), list, event.getX(), event.getY());
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }

    }

    private static boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY, int guiLeft, int guiTop) {
        mouseX = mouseX - (double)guiLeft;
        mouseY = mouseY - (double)guiTop;
        return mouseX >= (double)(x - 1) && mouseX < (double)(x + width + 1) && mouseY >= (double)(y - 1) && mouseY < (double)(y + height + 1);
    }

    private static ITextComponent getDisplayName(Enchantment enchantment, int level) {
        IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(enchantment.getName());
        if (enchantment.isCurse()) {
            iformattabletextcomponent.mergeStyle(TextFormatting.RED);
        } else {
            iformattabletextcomponent.mergeStyle(TextFormatting.AQUA);
        }

        if (level != 1 || enchantment.getMaxLevel() != 1) {
            iformattabletextcomponent.appendString(" ").appendSibling(new TranslationTextComponent("enchantment.level." + level));
        }

        return iformattabletextcomponent;
    }

}
