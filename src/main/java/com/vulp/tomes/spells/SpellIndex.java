package com.vulp.tomes.spells;

import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.init.SpellInit;
import net.minecraft.enchantment.Enchantment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum SpellIndex {

    SELF_PROPULSION(SpellInit.self_propulsion, EnchantmentInit.self_propulsion),
    FORCE_OF_WIND(SpellInit.force_of_wind, EnchantmentInit.force_of_wind),
    STRIKE_FROM_ABOVE(SpellInit.strike_from_above, EnchantmentInit.strike_from_above),
    DYING_KNOWLEDGE(SpellInit.dying_knowledge, EnchantmentInit.dying_knowledge),
    LINGUIST(SpellInit.linguist, EnchantmentInit.linguist),
    AIRY_PROTECTION(SpellInit.airy_protection, EnchantmentInit.airy_protection),
    EVERCHANGING_SKIES(SpellInit.everchanging_skies, EnchantmentInit.everchanging_skies),

    LIFEBRINGER(SpellInit.lifebringer, EnchantmentInit.lifebringer),
    BEAST_TAMER(SpellInit.beast_tamer, EnchantmentInit.beast_tamer),
    WILD_AID(SpellInit.wild_aid, EnchantmentInit.wild_aid),
    NURTURING_ROOTS(SpellInit.nurturing_roots, EnchantmentInit.nurturing_roots),
    ADVANTAGEOUS_GROWTH(SpellInit.advantageous_growth, EnchantmentInit.advantageous_growth),
    FOREST_AFFINITY(SpellInit.forest_affinity, EnchantmentInit.forest_affinity),
    WINGS_OF_NIGHT(SpellInit.wings_of_night, EnchantmentInit.wings_of_night),

    MIND_BENDER(SpellInit.mind_bender, EnchantmentInit.mind_bender),
    NECROTIC_STEED(SpellInit.necrotic_steed, EnchantmentInit.necrotic_steed),
    WITHERING_STENCH(SpellInit.withering_stench, EnchantmentInit.withering_stench),
    ROTTEN_HEART(SpellInit.rotten_heart, EnchantmentInit.rotten_heart),
    NOCTURNAL(SpellInit.nocturnal, EnchantmentInit.nocturnal),
    COVENS_RULE(SpellInit.covens_rule, EnchantmentInit.covens_rule),
    DARK_AGE(SpellInit.dark_age, EnchantmentInit.dark_age);

    private final Spell spell;
    private final Enchantment enchantment;

    SpellIndex(Spell spell, Enchantment enchantment) {
        this.spell = spell;
        this.enchantment = enchantment;
    }

    public static int idFromSpellIndex(SpellIndex spellIndex) {
        for (int i = 0; i < SpellIndex.values().length; i++) {
            if (SpellIndex.values()[i] == spellIndex) {
                return i;
            }
        }
        return -1;
    }

    public static int[] idArrayFromSpellIndexes(SpellIndex[] spellIndexes) {
        if (spellIndexes.length == 0) {
            return new int[]{-1};
        }
        int[] idList = new int[spellIndexes.length];
        for (int i = 0; i < spellIndexes.length; i++) {
            SpellIndex spellIndex = spellIndexes[i];
            idList[i] = idFromSpellIndex(spellIndex);
        }
        return idList;
    }

    public static SpellIndex spellIndexFromId(int id) {
        if (id == -1) {
            return null;
        }
        return SpellIndex.values()[id];
    }

    public static SpellIndex[] spellIndexArrayFromIds(int[] id) {
        if (Arrays.stream(id).anyMatch(value -> value == -1)) {
            return null;
        }
        SpellIndex[] spellIndexList = new SpellIndex[id.length];
        for (int i = 0; i < id.length; i++) {
            spellIndexList[i] = (spellIndexFromId(i));
        }
        return spellIndexList;
    }

    public Spell getSpell() {
        return this.spell;
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }



}
