package com.vulp.tomes.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class TomesConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> self_prop_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> self_prop_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> self_prop_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> force_of_wind_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> force_of_wind_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> force_of_wind_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> strike_from_above_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> strike_from_above_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> strike_from_above_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> astral_travel_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> astral_travel_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> astral_travel_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> everchanging_skies_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> everchanging_skies_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> everchanging_skies_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> dying_knowledge_enabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> linguist_enabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> airy_protection_enabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> cloudstep_enabled;

    public static final ForgeConfigSpec.ConfigValue<Boolean> lifebringer_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> lifebringer_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> lifebringer_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> beast_tamer_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> beast_tamer_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> beast_tamer_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> wild_aid_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> wild_aid_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> wild_aid_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> metamorphosis_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> metamorphosis_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> metamorphosis_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> molding_lands_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> molding_lands_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> molding_lands_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> nurturing_roots_enabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> advantageous_growth_enabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> forest_affinity_enabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> fight_or_flight_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> fight_or_flight_cooldown;

    public static final ForgeConfigSpec.ConfigValue<Boolean> mind_bender_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> mind_bender_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> mind_bender_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ghostly_steed_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> ghostly_steed_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ghostly_steed_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> withering_stench_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> withering_stench_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> withering_stench_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> deathly_ichor_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> deathly_ichor_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> deathly_ichor_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> dark_age_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> dark_age_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> dark_age_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Boolean> rotten_heart_enabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> nocturnal_enabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> covens_rule_enabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> borrowed_time_enabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> borrowed_time_cooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> archaic_heart_droprate;
    public static final ForgeConfigSpec.ConfigValue<Integer> beating_heart_droprate;
    public static final ForgeConfigSpec.ConfigValue<Integer> sweet_heart_droprate;

    static {
        BUILDER.push("Enabled/Disabled Spells: (All are set to true by default)");

        self_prop_enabled = BUILDER.define("Self Propulsion Enabled", true);
        force_of_wind_enabled = BUILDER.define("Force of Wind Enabled", true);
        strike_from_above_enabled = BUILDER.define("Strike From Above Enabled", true);
        astral_travel_enabled = BUILDER.define("Astral Travel Enabled", true);
        everchanging_skies_enabled = BUILDER.define("Everchanging Skies Enabled", true);
        dying_knowledge_enabled = BUILDER.define("Dying Knowledge Enabled", true);
        linguist_enabled = BUILDER.define("Linguist Enabled", true);
        airy_protection_enabled = BUILDER.define("Airy Protection Enabled", true);
        cloudstep_enabled = BUILDER.define("Cloudstep Enabled", true);

        lifebringer_enabled = BUILDER.define("Lifebringer Enabled", true);
        beast_tamer_enabled = BUILDER.define("Beast Tamer Enabled", true);
        wild_aid_enabled = BUILDER.define("Wild Aid Enabled", true);
        metamorphosis_enabled = BUILDER.define("Metamorphosis Enabled", true);
        molding_lands_enabled = BUILDER.define("Molding Lands Enabled", true);
        nurturing_roots_enabled = BUILDER.define("Nurturing Roots Enabled", true);
        advantageous_growth_enabled = BUILDER.define("Advantageous Growth Enabled", true);
        forest_affinity_enabled = BUILDER.define("Forest Affinity Enabled", true);
        fight_or_flight_enabled = BUILDER.define("Fight or Flight Enabled", true);
        fight_or_flight_cooldown = BUILDER.define("Fight or Flight Cooldown", 2400);

        mind_bender_enabled = BUILDER.define("Mind Bender Enabled", true);
        ghostly_steed_enabled = BUILDER.define("Ghostly Steed Enabled", true);
        withering_stench_enabled = BUILDER.define("Withering Stench Enabled", true);
        deathly_ichor_enabled = BUILDER.define("Deathly Ichor Enabled", true);
        dark_age_enabled = BUILDER.define("Dark Age Enabled", true);
        rotten_heart_enabled = BUILDER.define("Rotten Heart Enabled", true);
        nocturnal_enabled = BUILDER.define("Nocturnal Enabled", true);
        covens_rule_enabled = BUILDER.define("Coven's Rule Enabled", true);
        borrowed_time_enabled = BUILDER.define("Borrowed Time Enabled", true);
        borrowed_time_cooldown = BUILDER.define("Borrowed Time Cooldown", 6000);

        BUILDER.pop();
        BUILDER.push("Costs and Cooldowns: (Cooldowns are in ticks)");

        self_prop_cost = BUILDER.comment("DEFAULT = 8").define("Self Propulsion Cost", 8);
        self_prop_cooldown = BUILDER.comment("DEFAULT = 50").define("Self Propulsion Cooldown", 50);
        force_of_wind_cost = BUILDER.comment("DEFAULT = 10").define("Force of Wind Cost", 10);
        force_of_wind_cooldown = BUILDER.comment("DEFAULT = 70").define("Force of Wind Cooldown", 70);
        strike_from_above_cost = BUILDER.comment("DEFAULT = 40").define("Strike From Above Cost", 40);
        strike_from_above_cooldown = BUILDER.comment("DEFAULT = 200").define("Strike From Above Cooldown", 200);
        astral_travel_cost = BUILDER.comment("DEFAULT = 40").define("Astral Travel Cost", 40);
        astral_travel_cooldown = BUILDER.comment("DEFAULT = 200").define("Astral Travel Cooldown", 200);
        everchanging_skies_cost = BUILDER.comment("DEFAULT = 70").define("Everchanging Skies Cost", 70);
        everchanging_skies_cooldown = BUILDER.comment("DEFAULT = 600").define("Everchanging Skies Cooldown", 600);

        lifebringer_cost = BUILDER.comment("DEFAULT = 25").define("Lifebringer Cost", 25);
        lifebringer_cooldown = BUILDER.comment("DEFAULT = 80").define("Lifebringer Cooldown", 80);
        beast_tamer_cost = BUILDER.comment("DEFAULT = 60").define("Beast Tamer Cost", 60);
        beast_tamer_cooldown = BUILDER.comment("DEFAULT = 60").define("Beast Tamer Cooldown", 60);
        wild_aid_cost = BUILDER.comment("DEFAULT = 20").define("Wild Aid Cost", 20);
        wild_aid_cooldown = BUILDER.comment("DEFAULT = 80").define("Wild Aid Cooldown", 80);
        metamorphosis_cost = BUILDER.comment("DEFAULT = 80").define("Metamorphosis Cost", 80);
        metamorphosis_cooldown = BUILDER.comment("DEFAULT = 2000").define("Metamorphosis Cooldown", 2800);
        molding_lands_cost = BUILDER.comment("DEFAULT = 55").define("Molding Lands Cost", 55);
        molding_lands_cooldown = BUILDER.comment("DEFAULT = 35").define("Molding Lands Cooldown", 35);

        mind_bender_cost = BUILDER.comment("DEFAULT = 30").define("Mind Bender Cost", 30);
        mind_bender_cooldown = BUILDER.comment("DEFAULT = 50").define("Mind Bender Cooldown", 50);
        ghostly_steed_cost = BUILDER.comment("DEFAULT = 35").define("Ghostly Steed Cost", 35);
        ghostly_steed_cooldown = BUILDER.comment("DEFAULT = 200").define("Ghostly Steed Cooldown", 200);
        withering_stench_cost = BUILDER.comment("DEFAULT = 20").define("Withering Stench Cost", 20);
        withering_stench_cooldown = BUILDER.comment("DEFAULT = 35").define("Withering Stench Cooldown", 35);
        deathly_ichor_cost = BUILDER.comment("DEFAULT = 30").define("Deathly Ichor Cost", 30);
        deathly_ichor_cooldown = BUILDER.comment("DEFAULT = 65").define("Deathly Ichor Cooldown", 65);
        dark_age_cost = BUILDER.comment("DEFAULT = 70").define("Dark Age Cost", 70);
        dark_age_cooldown = BUILDER.comment("DEFAULT = 4000").define("Dark Age Cooldown", 4000);

        BUILDER.pop();
        BUILDER.push("Droprates: (0 is never drop, 100 is always drop)");

        archaic_heart_droprate = BUILDER.comment("DEFAULT = 2").define("Archaic Heart Droprate", 2);
        beating_heart_droprate = BUILDER.comment("DEFAULT = 7").define("Beating Heart Droprate", 7);
        sweet_heart_droprate = BUILDER.comment("DEFAULT = 20").define("Sweet Heart Droprate", 20);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
