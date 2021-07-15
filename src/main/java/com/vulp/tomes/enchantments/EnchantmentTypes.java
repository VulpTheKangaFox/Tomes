package com.vulp.tomes.enchantments;

import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.enchantment.EnchantmentType;

public class EnchantmentTypes {

    public static EnchantmentType ARCHAIC_TOME = EnchantmentType.create("archaic_tome", item -> item == ItemInit.archaic_tome);
    public static EnchantmentType LIVING_TOME = EnchantmentType.create("living_tome", item -> item == ItemInit.living_tome);
    public static EnchantmentType CURSED_TOME = EnchantmentType.create("cursed_tome", item -> item == ItemInit.cursed_tome);

}
