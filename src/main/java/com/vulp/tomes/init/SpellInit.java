package com.vulp.tomes.init;

import com.vulp.tomes.enchantments.TomeEnchantment;
import com.vulp.tomes.spells.Spell;
import com.vulp.tomes.spells.active.*;
import com.vulp.tomes.spells.passive.*;
import net.minecraft.enchantment.Enchantment;

public class SpellInit {

    public static Spell self_propulsion = new SelfPropulsionSpell(Enchantment.Rarity.UNCOMMON, true, false);
    public static Spell force_of_wind = new ForceOfWindSpell(Enchantment.Rarity.UNCOMMON, true, false);
    public static Spell strike_from_above = new StrikeFromAboveSpell(Enchantment.Rarity.UNCOMMON, true, false);
    public static Spell dying_knowledge = new DyingKnowledgeSpell(Enchantment.Rarity.COMMON, false, false);
    public static Spell linguist = new LinguistSpell(Enchantment.Rarity.COMMON, false, false);
    public static Spell airy_protection = new AiryProtectionSpell(Enchantment.Rarity.COMMON, false, false);
    public static Spell everchanging_skies = new EverchangingSkiesSpell(Enchantment.Rarity.RARE, true, true);

    public static Spell lifebringer = new LifebringerSpell(Enchantment.Rarity.UNCOMMON, true, false);
    public static Spell beast_tamer = new BeastTamerSpell(Enchantment.Rarity.UNCOMMON, true, false);
    public static Spell wild_aid = new WildAidSpell(Enchantment.Rarity.UNCOMMON, true, false);
    public static Spell nurturing_roots = new NurturingRootsSpell(Enchantment.Rarity.COMMON, false, false);
    public static Spell advantageous_growth = new AdvantageousGrowthSpell(Enchantment.Rarity.COMMON, false, false);
    public static Spell forest_affinity = new ForestAffinitySpell(Enchantment.Rarity.COMMON, false, false);
    public static Spell wings_of_night = new WingsOfNightSpell(Enchantment.Rarity.RARE, true, true);

    public static Spell mind_bender = new MindBenderSpell(Enchantment.Rarity.UNCOMMON, true, false);
    public static Spell ghostly_steed = new GhostlySteedSpell(Enchantment.Rarity.UNCOMMON, true, false);
    public static Spell withering_stench = new WitheringStenchSpell(Enchantment.Rarity.UNCOMMON, true, false);
    public static Spell rotten_heart = new WitheringStenchSpell(Enchantment.Rarity.UNCOMMON, true, false); // PLACEHOLDER TO NOT NULL
    public static Spell nocturnal = new WitheringStenchSpell(Enchantment.Rarity.UNCOMMON, true, false); // PLACEHOLDER TO NOT NULL
    public static Spell covens_rule = new CovensRuleSpell(Enchantment.Rarity.COMMON, false, false);
    public static Spell dark_age = new DarkAgeSpell(Enchantment.Rarity.RARE, true, true);

}
