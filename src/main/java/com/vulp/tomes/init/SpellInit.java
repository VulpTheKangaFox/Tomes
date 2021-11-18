package com.vulp.tomes.init;

import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.spells.Spell;
import com.vulp.tomes.spells.active.*;
import com.vulp.tomes.spells.passive.*;
import net.minecraft.enchantment.Enchantment;

// NOTE: Came to a recent revelation that since all the spells are static and there's no non-static handler class, all players
//  share the same static spell class for each spell. Gonna leave it until it needs to be fixed, since I think there's a chance
//  it doesn't cause any significant issues.

public class SpellInit {

    public static Spell self_propulsion = new SelfPropulsionSpell(Enchantment.Rarity.UNCOMMON, true, false, TomesConfig.self_prop_enabled);
    public static Spell force_of_wind = new ForceOfWindSpell(Enchantment.Rarity.UNCOMMON, true, false, TomesConfig.force_of_wind_enabled);
    public static Spell strike_from_above = new StrikeFromAboveSpell(Enchantment.Rarity.UNCOMMON, true, false, TomesConfig.strike_from_above_enabled);
    public static Spell dying_knowledge = new DyingKnowledgeSpell(Enchantment.Rarity.COMMON, false, false, TomesConfig.dying_knowledge_enabled);
    public static Spell linguist = new LinguistSpell(Enchantment.Rarity.COMMON, false, false, TomesConfig.linguist_enabled);
    public static Spell airy_protection = new AiryProtectionSpell(Enchantment.Rarity.COMMON, false, false, TomesConfig.airy_protection_enabled);
    public static Spell everchanging_skies = new EverchangingSkiesSpell(Enchantment.Rarity.RARE, true, true, TomesConfig.everchanging_skies_enabled);

    public static Spell lifebringer = new LifebringerSpell(Enchantment.Rarity.UNCOMMON, true, false, TomesConfig.lifebringer_enabled);
    public static Spell beast_tamer = new BeastTamerSpell(Enchantment.Rarity.UNCOMMON, true, false, TomesConfig.beast_tamer_enabled);
    public static Spell wild_aid = new WildAidSpell(Enchantment.Rarity.UNCOMMON, true, false, TomesConfig.wild_aid_enabled);
    public static Spell nurturing_roots = new NurturingRootsSpell(Enchantment.Rarity.COMMON, false, false, TomesConfig.nurturing_roots_enabled);
    public static Spell advantageous_growth = new AdvantageousGrowthSpell(Enchantment.Rarity.COMMON, false, false, TomesConfig.advantageous_growth_enabled);
    public static Spell forest_affinity = new ForestAffinitySpell(Enchantment.Rarity.COMMON, false, false, TomesConfig.forest_affinity_enabled);
    public static Spell molding_lands = new MoldingLandsSpell(Enchantment.Rarity.RARE, true, true, TomesConfig.molding_lands_enabled);

    public static Spell mind_bender = new MindBenderSpell(Enchantment.Rarity.UNCOMMON, true, false, TomesConfig.mind_bender_enabled);
    public static Spell ghostly_steed = new GhostlySteedSpell(Enchantment.Rarity.UNCOMMON, true, false, TomesConfig.ghostly_steed_enabled);
    public static Spell withering_stench = new WitheringStenchSpell(Enchantment.Rarity.UNCOMMON, true, false, TomesConfig.withering_stench_enabled);
    public static Spell rotten_heart = new RottenHeartSpell(Enchantment.Rarity.COMMON, false, false, TomesConfig.rotten_heart_enabled);
    public static Spell nocturnal = new NocturnalSpell(Enchantment.Rarity.COMMON, false, false, TomesConfig.nocturnal_enabled);
    public static Spell covens_rule = new CovensRuleSpell(Enchantment.Rarity.COMMON, false, false, TomesConfig.covens_rule_enabled);
    public static Spell dark_age = new DarkAgeSpell(Enchantment.Rarity.RARE, true, true, TomesConfig.dark_age_enabled);

}
