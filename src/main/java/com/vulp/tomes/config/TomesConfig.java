package com.vulp.tomes.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class TomesConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> self_prop_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> self_prop_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> force_of_wind_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> force_of_wind_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> strike_from_above_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> strike_from_above_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> everchanging_skies_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> everchanging_skies_cooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> lifebringer_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> lifebringer_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> beast_tamer_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> beast_tamer_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> wild_aid_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> wild_aid_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> molding_lands_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> molding_lands_cooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> mind_bender_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> mind_bender_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ghostly_steed_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ghostly_steed_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> withering_stench_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> withering_stench_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> dark_age_cost;
    public static final ForgeConfigSpec.ConfigValue<Integer> dark_age_cooldown;

    static {
        BUILDER.push("Costs and Cooldowns:").comment("Cost means amount of tome damage, and cooldown is in ticks.");
        self_prop_cost = BUILDER.comment("DEFAULT = 8").define("Self Propulsion Cost", 8);
        self_prop_cooldown = BUILDER.comment("DEFAULT = 50").define("Self Propulsion Cooldown", 50);
        force_of_wind_cost = BUILDER.comment("DEFAULT = 10").define("Force of Wind Cost", 10);
        force_of_wind_cooldown = BUILDER.comment("DEFAULT = 70").define("Force of Wind Cooldown", 70);
        strike_from_above_cost = BUILDER.comment("DEFAULT = 40").define("Strike From Above Cost", 40);
        strike_from_above_cooldown = BUILDER.comment("DEFAULT = 200").define("Strike From Above Cooldown", 200);
        everchanging_skies_cost = BUILDER.comment("DEFAULT = 70").define("Everchanging Skies Cost", 70);
        everchanging_skies_cooldown = BUILDER.comment("DEFAULT = 600").define("Everchanging Skies Cooldown", 600);

        lifebringer_cost = BUILDER.comment("DEFAULT = 25").define("Lifebringer Cost", 25);
        lifebringer_cooldown = BUILDER.comment("DEFAULT = 80").define("Lifebringer Cooldown", 80);
        beast_tamer_cost = BUILDER.comment("DEFAULT = 60").define("Beast Tamer Cost", 60);
        beast_tamer_cooldown = BUILDER.comment("DEFAULT = 60").define("Beast Tamer Cooldown", 60);
        wild_aid_cost = BUILDER.comment("DEFAULT = 20").define("Wild Aid Cost", 20);
        wild_aid_cooldown = BUILDER.comment("DEFAULT = 80").define("Wild Aid Cooldown", 80);
        molding_lands_cost = BUILDER.comment("DEFAULT = 55").define("Molding Lands Cost", 55);
        molding_lands_cooldown = BUILDER.comment("DEFAULT = 35").define("Molding Lands Cooldown", 35);

        mind_bender_cost = BUILDER.comment("DEFAULT = 30").define("Mind Bender Cost", 30);
        mind_bender_cooldown = BUILDER.comment("DEFAULT = 50").define("Mind Bender Cooldown", 50);
        ghostly_steed_cost = BUILDER.comment("DEFAULT = 35").define("Ghostly Steed Cost", 35);
        ghostly_steed_cooldown = BUILDER.comment("DEFAULT = 200").define("Ghostly Steed Cooldown", 200);
        withering_stench_cost = BUILDER.comment("DEFAULT = 20").define("Withering Stench Cost", 20);
        withering_stench_cooldown = BUILDER.comment("DEFAULT = 35").define("Withering Stench Cooldown", 35);
        dark_age_cost = BUILDER.comment("DEFAULT = 70").define("Dark Age Cost", 70);
        dark_age_cooldown = BUILDER.comment("DEFAULT = 4000").define("Dark Age Cooldown", 4000);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
