package com.vulp.tomes.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.spells.SpellIndex;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WitchTrades extends VillagerTrades {

    private static final List<Enchantment> TREASURE_SPELL_ENCHANTS = Arrays.asList(EnchantmentInit.dark_age, EnchantmentInit.wings_of_night, EnchantmentInit.everchanging_skies);

    public static final Int2ObjectMap<VillagerTrades.ITrade[]> WITCH_TRADES = gatAsIntMap(ImmutableMap.of(
            1,
            new VillagerTrades.ITrade[]{
                    new VillagerTrades.ItemsForEmeraldsTrade(Items.NETHER_WART, 4, 8, 2147483647, 5),
                    new VillagerTrades.EmeraldForItemsTrade(Items.SPIDER_EYE, 18, 2147483647, 4)},
            2,
            new VillagerTrades.ITrade[]{
                    new VillagerTrades.EmeraldForItemsTrade(Items.EXPERIENCE_BOTTLE, 3, 2147483647, 7),
                    new VillagerTrades.ItemsForEmeraldsTrade(Items.EXPERIENCE_BOTTLE, 7, 1, 2147483647, 6)},
            3,
            new VillagerTrades.ITrade[]{
                    new VillagerTrades.ItemsForEmeraldsTrade(Items.BLAZE_ROD, 5, 2147483647, 8),
                    new ItemWithPotionEffectForEmeralds(Items.GLASS_BOTTLE, Potions.REGENERATION, 5, 2147483647, 8)},
            4,
            new VillagerTrades.ITrade[]{
                    new VillagerTrades.ItemsForEmeraldsTrade(Items.TOTEM_OF_UNDYING, 50, 1, 1, 24),
                    new RareEnchantBookForEmeralds(20),
                    new VillagerTrades.ItemsForEmeraldsTrade(Items.APPLE, 30, 1, 1, 20)}));

    private static Int2ObjectMap<VillagerTrades.ITrade[]> gatAsIntMap(ImmutableMap<Integer, VillagerTrades.ITrade[]> p_221238_0_) {
        return new Int2ObjectOpenHashMap<>(p_221238_0_);
    }

    static class RareEnchantBookForEmeralds implements VillagerTrades.ITrade {
        private final int xpValue;

        public RareEnchantBookForEmeralds(int xpValueIn) {
            this.xpValue = xpValueIn;
        }

        public MerchantOffer getOffer(Entity trader, Random rand) {
            Enchantment enchantment = TREASURE_SPELL_ENCHANTS.get(rand.nextInt(TREASURE_SPELL_ENCHANTS.size()));
            ItemStack itemstack = EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(enchantment, 1));
            return new MerchantOffer(new ItemStack(Items.EMERALD, rand.nextInt(25) + 20), new ItemStack(Items.BOOK), itemstack, 1, this.xpValue, 0.2F);
        }
    }

    static class ItemWithPotionEffectForEmeralds implements VillagerTrades.ITrade {
        private final ItemStack sellingItem;
        private final Potion potion;
        private final int emeraldCount;
        private final int sellingItemCount;
        private final int maxUses;
        private final int xpValue;
        private final float priceMultiplier;

        public ItemWithPotionEffectForEmeralds(Item sellingItem, Potion potion, int emeraldCount, int sellingItemCount, int xpValue) {
            this(new ItemStack(sellingItem), potion, emeraldCount, sellingItemCount, 12, xpValue);
        }

        public ItemWithPotionEffectForEmeralds(Item sellingItem, Potion potion, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
            this(new ItemStack(sellingItem), potion,  emeraldCount, sellingItemCount, maxUses, xpValue);
        }

        public ItemWithPotionEffectForEmeralds(ItemStack sellingItem, Potion potion, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
            this(sellingItem, potion,  emeraldCount, sellingItemCount, maxUses, xpValue, 0.05F);
        }

        public ItemWithPotionEffectForEmeralds(ItemStack sellingItem, Potion potion, int emeraldCount, int sellingItemCount, int maxUses, int xpValue, float priceMultiplier) {
            this.sellingItem = sellingItem;
            this.potion = potion;
            this.emeraldCount = emeraldCount;
            this.sellingItemCount = sellingItemCount;
            this.maxUses = maxUses;
            this.xpValue = xpValue;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(Entity trader, Random rand) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCount), PotionUtils.addPotionToItemStack(new ItemStack(this.sellingItem.getItem(), this.sellingItemCount), this.potion), this.maxUses, this.xpValue, this.priceMultiplier);
        }
    }

}
