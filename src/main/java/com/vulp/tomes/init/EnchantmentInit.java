package com.vulp.tomes.init;

import com.vulp.tomes.enchantments.EnchantmentTypes;
import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.spells.SpellIndex;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EnchantmentInit {

    public static HashMap<Enchantment, TranslationTextComponent> TOME_DESCRIPTIONS = new HashMap<>();
    public static List<Enchantment> TOME_LIST = new java.util.ArrayList<>(Collections.emptyList());
    public static List<Enchantment> TREASURE_TOME_ENCHANTS = new java.util.ArrayList<>(Collections.emptyList());

    public static Enchantment self_propulsion = createTomeEnchant(SpellIndex.SELF_PROPULSION, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment force_of_wind = createTomeEnchant(SpellIndex.FORCE_OF_WIND, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment strike_from_above = createTomeEnchant(SpellIndex.STRIKE_FROM_ABOVE, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment everchanging_skies = createTomeEnchant(SpellIndex.EVERCHANGING_SKIES, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment dying_knowledge = createTomeEnchant(SpellIndex.DYING_KNOWLEDGE, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment linguist = createTomeEnchant(SpellIndex.LINGUIST, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment airy_protection = createTomeEnchant(SpellIndex.AIRY_PROTECTION, EnchantmentTypes.ARCHAIC_TOME);

    public static Enchantment lifebringer = createTomeEnchant(SpellIndex.LIFEBRINGER, EnchantmentTypes.LIVING_TOME);
    public static Enchantment beast_tamer = createTomeEnchant(SpellIndex.BEAST_TAMER, EnchantmentTypes.LIVING_TOME);
    public static Enchantment wild_aid = createTomeEnchant(SpellIndex.WILD_AID, EnchantmentTypes.LIVING_TOME);
    public static Enchantment molding_lands = createTomeEnchant(SpellIndex.WINGS_OF_NIGHT, EnchantmentTypes.LIVING_TOME);
    public static Enchantment nurturing_roots = createTomeEnchant(SpellIndex.NURTURING_ROOTS, EnchantmentTypes.LIVING_TOME);
    public static Enchantment advantageous_growth = createTomeEnchant(SpellIndex.ADVANTAGEOUS_GROWTH, EnchantmentTypes.LIVING_TOME);
    public static Enchantment forest_affinity = createTomeEnchant(SpellIndex.FOREST_AFFINITY, EnchantmentTypes.LIVING_TOME);

    public static Enchantment mind_bender = createTomeEnchant(SpellIndex.MIND_BENDER, EnchantmentTypes.CURSED_TOME);
    public static Enchantment ghostly_steed = createTomeEnchant(SpellIndex.GHOSTLY_STEED, EnchantmentTypes.CURSED_TOME);
    public static Enchantment withering_stench = createTomeEnchant(SpellIndex.WITHERING_STENCH, EnchantmentTypes.CURSED_TOME);
    public static Enchantment dark_age = createTomeEnchant(SpellIndex.DARK_AGE, EnchantmentTypes.CURSED_TOME);
    public static Enchantment rotten_heart = createTomeEnchant(SpellIndex.ROTTEN_HEART, EnchantmentTypes.CURSED_TOME);
    public static Enchantment nocturnal = createTomeEnchant(SpellIndex.NOCTURNAL, EnchantmentTypes.CURSED_TOME);
    public static Enchantment covens_rule = createTomeEnchant(SpellIndex.COVENS_RULE, EnchantmentTypes.CURSED_TOME);

    protected static TomeEnchantment createTomeEnchant(SpellIndex index, EnchantmentType type) {
        TomeEnchantment enchant = new TomeEnchantment(index, type);
        if (index.getSpell().isRare()) {
            TREASURE_TOME_ENCHANTS.add(enchant);
        }
        TOME_LIST.add(enchant);
        return enchant;
    }

    public static void setupDescriptions() {
        for (Enchantment enchantment : TOME_LIST) {
            TOME_DESCRIPTIONS.put(enchantment, new TranslationTextComponent(enchantment.getName() + ".desc"));
        }
    }

}
