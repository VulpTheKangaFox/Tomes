package com.vulp.tomes.mixin;

import com.google.common.collect.Lists;
import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(at = @At("HEAD"), method = "buildEnchantmentList", cancellable = true)
    private static void buildEnchantmentList(Random randomIn, ItemStack itemStackIn, int level, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentData>> info) {
        if (itemStackIn.getItem() instanceof TomeItem) {
            List<EnchantmentData> list = Lists.newArrayList();

            List<EnchantmentData> list1 = new java.util.ArrayList<>(Collections.emptyList());
            for(Enchantment enchantment : Registry.ENCHANTMENT) {
                if (enchantment instanceof TomeEnchantment && (!enchantment.isTreasureEnchantment() || allowTreasure)) {
                    if (enchantment.canApplyAtEnchantingTable(itemStackIn)) {
                        list1.add(new EnchantmentData(enchantment, 1));
                    }
                }
            }

            if (!list1.isEmpty()) {
                while (list.isEmpty()) {
                    EnchantmentData data = WeightedRandom.getRandomItem(randomIn, list1);
                    if (!((TomeEnchantment)data.enchantment).isActive()) {
                        list.add(data);
                    }
                }
                if (level > 8 + randomIn.nextInt(4)) {
                    while (list.size() < 2) {
                        EnchantmentData data = WeightedRandom.getRandomItem(randomIn, list1);
                        if (!list.contains(data) && !((TomeEnchantment)data.enchantment).isActive()) {
                            list.add(data);
                        }
                    }
                }
                if (level >= 30) {
                    while (list.size() < 3) {
                        EnchantmentData data = WeightedRandom.getRandomItem(randomIn, list1);
                        if (((TomeEnchantment)data.enchantment).isActive()) {
                            list.add(data);
                        }
                    }
                }
            }

            info.setReturnValue(list);
        }
    }

}
