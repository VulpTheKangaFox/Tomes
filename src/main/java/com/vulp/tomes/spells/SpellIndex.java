package com.vulp.tomes.spells;

import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.init.SpellInit;
import net.minecraft.enchantment.Enchantment;

import java.util.Collections;
import java.util.List;

public enum SpellIndex {

    // Spells from here-on should be added in order otherwise some issues with id back and forthing will occur.
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
    WINGS_OF_NIGHT(SpellInit.molding_lands, EnchantmentInit.molding_lands),

    MIND_BENDER(SpellInit.mind_bender, EnchantmentInit.mind_bender),
    GHOSTLY_STEED(SpellInit.ghostly_steed, EnchantmentInit.ghostly_steed),
    WITHERING_STENCH(SpellInit.withering_stench, EnchantmentInit.withering_stench),
    ROTTEN_HEART(SpellInit.rotten_heart, EnchantmentInit.rotten_heart),
    NOCTURNAL(SpellInit.nocturnal, EnchantmentInit.nocturnal),
    COVENS_RULE(SpellInit.covens_rule, EnchantmentInit.covens_rule),
    DARK_AGE(SpellInit.dark_age, EnchantmentInit.dark_age),

    ASTRAL_TRAVEL(SpellInit.astral_travel, EnchantmentInit.astral_travel),
    CLOUDSTEP(SpellInit.cloudstep, EnchantmentInit.cloudstep),
    METAMORPHOSIS(SpellInit.metamorphosis, EnchantmentInit.metamorphosis),
    FIGHT_OR_FLIGHT(SpellInit.fight_or_flight, EnchantmentInit.fight_or_flight),
    DEATHLY_ICHOR(SpellInit.deathly_ichor, EnchantmentInit.deathly_ichor),
    BORROWED_TIME(SpellInit.borrowed_time, EnchantmentInit.borrowed_time);

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
        int count = 0;
        SpellIndex[] spellIndexList = new SpellIndex[id.length];
        for (int index : id) {
            if (index == -1) {
                return null;
            } else {
                spellIndexList[count] = SpellIndex.values()[index];
                count++;
            }
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
