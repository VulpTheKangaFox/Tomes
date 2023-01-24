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

    public static Spell self_propulsion = new SelfPropulsionSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.self_prop_enabled);
    public static Spell force_of_wind = new ForceOfWindSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.force_of_wind_enabled);
    public static Spell strike_from_above = new StrikeFromAboveSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.strike_from_above_enabled);
    public static Spell astral_travel = new AstralTravelSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.astral_travel_enabled);
    public static Spell dying_knowledge = new DyingKnowledgeSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.dying_knowledge_enabled);
    public static Spell linguist = new LinguistSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.linguist_enabled);
    public static Spell airy_protection = new AiryProtectionSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.airy_protection_enabled);
    public static Spell cloudstep = new CloudstepSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.cloudstep_enabled);
    public static Spell everchanging_skies = new EverchangingSkiesSpell(Enchantment.Rarity.VERY_RARE, true, true, TomesConfig.everchanging_skies_enabled);

    public static Spell lifebringer = new LifebringerSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.lifebringer_enabled);
    public static Spell beast_tamer = new BeastTamerSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.beast_tamer_enabled);
    public static Spell wild_aid = new WildAidSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.wild_aid_enabled);
    public static Spell metamorphosis = new MetamorphosisSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.metamorphosis_enabled);
    public static Spell nurturing_roots = new NurturingRootsSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.nurturing_roots_enabled);
    public static Spell advantageous_growth = new AdvantageousGrowthSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.advantageous_growth_enabled);
    public static Spell forest_affinity = new ForestAffinitySpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.forest_affinity_enabled);
    public static Spell fight_or_flight = new FightOrFlightSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.fight_or_flight_enabled);
    public static Spell molding_lands = new MoldingLandsSpell(Enchantment.Rarity.VERY_RARE, true, true, TomesConfig.molding_lands_enabled);

    public static Spell mind_bender = new MindBenderSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.mind_bender_enabled);
    public static Spell ghostly_steed = new GhostlySteedSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.ghostly_steed_enabled);
    public static Spell withering_stench = new WitheringStenchSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.withering_stench_enabled);
    public static Spell deathly_ichor = new DeathlyIchorSpell(Enchantment.Rarity.RARE, true, false, TomesConfig.deathly_ichor_enabled);
    public static Spell rotten_heart = new RottenHeartSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.rotten_heart_enabled);
    public static Spell nocturnal = new NocturnalSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.nocturnal_enabled);
    public static Spell covens_rule = new CovensRuleSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.covens_rule_enabled);
    public static Spell borrowed_time = new BorrowedTimeSpell(Enchantment.Rarity.UNCOMMON, false, false, TomesConfig.borrowed_time_enabled);
    public static Spell dark_age = new DarkAgeSpell(Enchantment.Rarity.VERY_RARE, true, true, TomesConfig.dark_age_enabled);

}
