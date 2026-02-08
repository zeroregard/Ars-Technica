package com.zeroregard.ars_technica.saucelib.api.compound;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for the "cluster" pattern: a glyph knows glyphs ahead until it spots a "stranger".
 * Parts that are compatible augments or declared subsequent effects belong to the cluster.
 */
public final class SubsequentContextHelper {

    private SubsequentContextHelper() {}

    /**
     * Index of the first effect in the spell, or -1 if none.
     * Spell shape is typically [method?, effect, augments...].
     */
    public static int findFirstEffectIndex(List<AbstractSpellPart> spell) {
        if (spell == null) return -1;
        for (int i = 0; i < spell.size(); i++) {
            if (spell.get(i) instanceof AbstractEffect) return i;
        }
        return -1;
    }

    /**
     * Returns the cluster for the effect at {@code effectIndex}: the effect plus all following
     * parts that are either a compatible augment or a declared subsequent effect. Stops at the
     * first "stranger" (part that does not belong).
     *
     * @param spell       the full spell
     * @param effectIndex index where an AbstractEffect implementing ISubsequentEffectProvider sits
     * @return sublist from effectIndex (inclusive) to first stranger (exclusive), or empty if invalid
     */
    public static List<AbstractSpellPart> getCluster(List<AbstractSpellPart> spell, int effectIndex) {
        if (spell == null || effectIndex < 0 || effectIndex >= spell.size()) return List.of();
        AbstractSpellPart effect = spell.get(effectIndex);
        if (!(effect instanceof AbstractEffect) || !(effect instanceof ISubsequentEffectProvider provider)) {
            return List.of();
        }
        List<AbstractSpellPart> cluster = new ArrayList<>();
        cluster.add(effect);
        for (int i = effectIndex + 1; i < spell.size(); i++) {
            AbstractSpellPart part = spell.get(i);
            if (part == null || !provider.isPartInCluster(part)) break;
            cluster.add(part);
        }
        return cluster;
    }
}
