package com.zeroregard.ars_technica.client.gui;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.zeroregard.ars_technica.saucelib.api.compound.ISubsequentEffectProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks the current spell in the spell crafting UI so we can show
 * "subsequent effect" tooltips (e.g. Smelt after Press with Extract).
 */
public class SpellCompositeContext {

    private static SpellCompositeContext instance;

    private List<AbstractSpellPart> currentSpell = new ArrayList<>();
    /** Button instance -> index in spell. Only buttons that are spell-strip slots (not grid). */
    private final Map<Object, Integer> spellStripSlotIndices = new HashMap<>();

    public static SpellCompositeContext getInstance() {
        if (instance == null) {
            instance = new SpellCompositeContext();
        }
        return instance;
    }

    public void setCurrentSpell(List<AbstractSpellPart> spell) {
        this.currentSpell = spell != null ? new ArrayList<>(spell) : new ArrayList<>();
        spellStripSlotIndices.clear();
    }

    /** Call when the book has built the spell strip UI; records which buttons are spell-strip slots. */
    public void setSpellStripSlot(Object button, int indexInSpell) {
        if (button != null && indexInSpell >= 0) spellStripSlotIndices.put(button, indexInSpell);
    }

    /** Index of this button in the spell if it is a spell-strip slot, otherwise -1. */
    public int getSpellStripIndex(Object button) {
        return button != null ? spellStripSlotIndices.getOrDefault(button, -1) : -1;
    }

    /** True if this button is a spell-strip slot (so we should show "Active" not "Possible"). */
    public boolean isSpellStripSlot(Object button) {
        return getSpellStripIndex(button) >= 0;
    }

    /** Current spell (unmodifiable). Empty if none. */
    public List<AbstractSpellPart> getCurrentSpell() {
        return Collections.unmodifiableList(currentSpell);
    }

    /** True if the given part is in the current spell (by registry name, so strip buttons match even if different instance). */
    public boolean isPartInSpell(AbstractSpellPart part) {
        return indexOfPartInSpell(part) >= 0;
    }

    /** Index of the first occurrence of part in the current spell (by registry name), or -1. */
    public int indexOfPartInSpell(AbstractSpellPart part) {
        if (part == null) return -1;
        ResourceLocation id = part.getRegistryName();
        if (id == null) return -1;
        for (int i = 0; i < currentSpell.size(); i++) {
            AbstractSpellPart p = currentSpell.get(i);
            if (p != null && id.equals(p.getRegistryName())) return i;
        }
        return -1;
    }

    /** Last effect in the current spell (by order in list). */
    public AbstractSpellPart getLastEffect() {
        if (currentSpell.isEmpty()) return null;
        for (int i = currentSpell.size() - 1; i >= 0; i--) {
            AbstractSpellPart part = currentSpell.get(i);
            if (part instanceof AbstractEffect) return part;
        }
        return null;
    }

    /** True if the given glyph is a valid "next" effect for the last effect (e.g. Smelt for Press). */
    public boolean isSubsequentEffect(ResourceLocation glyphId) {
        AbstractSpellPart lastEffect = getLastEffect();
        if (lastEffect == null) return false;
        if (!(lastEffect instanceof ISubsequentEffectProvider provider)) return false;
        ResourceLocation[] subsequentGlyphs = provider.getSubsequentEffectGlyphs();
        if (subsequentGlyphs == null || subsequentGlyphs.length == 0) return false;
        for (ResourceLocation id : subsequentGlyphs) {
            if (id != null && id.equals(glyphId)) return true;
        }
        return false;
    }
}
