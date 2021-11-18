package com.vulp.tomes.entities;

import com.google.common.collect.ImmutableMap;
import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.init.EnchantmentInit;
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

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class WitchTrades extends VillagerTrades {

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
                    new VillagerTrades.ItemsForEmeraldsTrade(Items.BLAZE_ROD, 5, 1,2147483647, 8),
                    new ItemWithPotionEffectForEmeralds(Items.POTION, Potions.REGENERATION, 5, 2147483647, 8)},
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
            Stream<Enchantment> enchants = EnchantmentInit.TREASURE_TOME_ENCHANTS.stream().filter(ench -> !(((TomeEnchantment)ench).getSpellIndex().getSpell().isDisabled()));
            Enchantment enchantment;
            try {
                enchantment = enchants.skip(rand.nextInt((int) enchants.count())).findFirst().get(); // Hope this works?
            } catch (NoSuchElementException exception) {
                return new MerchantOffer(ItemStack.EMPTY, ItemStack.EMPTY, 1, 0, 0);
            }
            ItemStack itemstack = EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(enchantment, 1));
            return new MerchantOffer(new ItemStack(Items.EMERALD, rand.nextInt(25) + 20), new ItemStack(Items.BOOK), itemstack, 1, this.xpValue, 0.2F);
        }
    }

    static class ItemWithPotionEffectForEmeralds implements VillagerTrades.ITrade {
        private final Item potionTypeItem;
        private final Potion potion;
        private final int emeraldCount;
        private final int maxUses;
        private final int xpValue;
        private final float priceMultiplier;

        public ItemWithPotionEffectForEmeralds(Item potionTypeItem, Potion potion, int emeraldCount, int maxUses, int xpValue) {
            this(potionTypeItem, potion,  emeraldCount, maxUses, xpValue, 0.05F);
        }

        public ItemWithPotionEffectForEmeralds(Item potionTypeItem, Potion potion, int emeraldCount, int maxUses, int xpValue, float priceMultiplier) {
            this.potionTypeItem = potionTypeItem;
            this.potion = potion;
            this.emeraldCount = emeraldCount;
            this.maxUses = maxUses;
            this.xpValue = xpValue;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(Entity trader, Random rand) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCount), new ItemStack(Items.GLASS_BOTTLE), PotionUtils.addPotionToItemStack(new ItemStack(this.potionTypeItem), this.potion), this.maxUses, this.xpValue, this.priceMultiplier);
        }
    }

}
