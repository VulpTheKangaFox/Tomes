package com.vulp.tomes.init;

import com.vulp.tomes.enchantments.EnchantmentTypes;
import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.spells.SpellIndex;
import net.minecraft.enchantment.Enchantment;

public class EnchantmentInit {

    public static Enchantment self_propulsion = new TomeEnchantment(SpellIndex.SELF_PROPULSION, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment force_of_wind = new TomeEnchantment(SpellIndex.FORCE_OF_WIND, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment strike_from_above = new TomeEnchantment(SpellIndex.STRIKE_FROM_ABOVE, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment dying_knowledge = new TomeEnchantment(SpellIndex.DYING_KNOWLEDGE, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment linguist = new TomeEnchantment(SpellIndex.LINGUIST, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment airy_protection = new TomeEnchantment(SpellIndex.AIRY_PROTECTION, EnchantmentTypes.ARCHAIC_TOME);
    public static Enchantment everchanging_skies = new TomeEnchantment(SpellIndex.EVERCHANGING_SKIES, EnchantmentTypes.ARCHAIC_TOME); // PLACEHOLDER TO PREVENT NULLS

    public static Enchantment lifebringer = new TomeEnchantment(SpellIndex.LIFEBRINGER, EnchantmentTypes.LIVING_TOME);
    public static Enchantment beast_tamer = new TomeEnchantment(SpellIndex.BEAST_TAMER, EnchantmentTypes.LIVING_TOME);
    public static Enchantment wild_aid = new TomeEnchantment(SpellIndex.WILD_AID, EnchantmentTypes.LIVING_TOME);
    public static Enchantment nurturing_roots = new TomeEnchantment(SpellIndex.NURTURING_ROOTS, EnchantmentTypes.LIVING_TOME);
    public static Enchantment advantageous_growth = new TomeEnchantment(SpellIndex.ADVANTAGEOUS_GROWTH, EnchantmentTypes.LIVING_TOME);
    public static Enchantment forest_affinity = new TomeEnchantment(SpellIndex.FOREST_AFFINITY, EnchantmentTypes.LIVING_TOME);
    public static Enchantment wings_of_night = new TomeEnchantment(SpellIndex.WINGS_OF_NIGHT, EnchantmentTypes.LIVING_TOME); // PLACEHOLDER TO PREVENT NULLS

    public static Enchantment mind_bender = new TomeEnchantment(SpellIndex.MIND_BENDER, EnchantmentTypes.CURSED_TOME);
    public static Enchantment ghostly_steed = new TomeEnchantment(SpellIndex.GHOSTLY_STEED, EnchantmentTypes.CURSED_TOME);
    public static Enchantment withering_stench = new TomeEnchantment(SpellIndex.WITHERING_STENCH, EnchantmentTypes.CURSED_TOME);
    public static Enchantment rotten_heart = new TomeEnchantment(SpellIndex.WITHERING_STENCH, EnchantmentTypes.CURSED_TOME); // DUPLICATE TO PREVENT NULLS
    public static Enchantment nocturnal = new TomeEnchantment(SpellIndex.WITHERING_STENCH, EnchantmentTypes.CURSED_TOME); // DUPLICATE TO PREVENT NULLS
    public static Enchantment covens_rule = new TomeEnchantment(SpellIndex.COVENS_RULE, EnchantmentTypes.CURSED_TOME);
    public static Enchantment dark_age = new TomeEnchantment(SpellIndex.DARK_AGE, EnchantmentTypes.CURSED_TOME); // PLACEHOLDER TO PREVENT NULLS

}
