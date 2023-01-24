package com.vulp.tomes.items;

import com.vulp.tomes.init.ItemInit;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class HiddenDescriptorItem extends Item {

    public HiddenDescriptorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null) return;
        IFormattableTextComponent textComponent1 = null;
        IFormattableTextComponent textComponent2 = null;
        Item item = stack.getItem();

        if (item == ItemInit.ancient_heart) {
            textComponent1 = new TranslationTextComponent("item.tomes.ancient_heart_desc1").mergeStyle(TextFormatting.GOLD);
            textComponent2 = new TranslationTextComponent("item.tomes.ancient_heart_desc2").mergeStyle(TextFormatting.YELLOW, TextFormatting.ITALIC);
        } else if (item == ItemInit.beating_heart) {
            textComponent1 = new TranslationTextComponent("item.tomes.beating_heart_desc1").mergeStyle(TextFormatting.DARK_GREEN);
            textComponent2 = new TranslationTextComponent("item.tomes.beating_heart_desc2").mergeStyle(TextFormatting.GREEN, TextFormatting.ITALIC);
        } else if (item == ItemInit.sweet_heart) {
            textComponent1 = new TranslationTextComponent("item.tomes.sweet_heart_desc1").mergeStyle(TextFormatting.DARK_RED);
            textComponent2 = new TranslationTextComponent("item.tomes.sweet_heart_desc2").mergeStyle(TextFormatting.RED, TextFormatting.ITALIC);
        }

        if (textComponent1 != null) {
            if (!Screen.hasShiftDown()) {
                tooltip.add(new TranslationTextComponent("item.tomes.hold_shift").mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
            } else {
                tooltip.add(textComponent1);
                if (textComponent2 != null) {
                    tooltip.add(textComponent2);
                }
            }
        } else {
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

}
