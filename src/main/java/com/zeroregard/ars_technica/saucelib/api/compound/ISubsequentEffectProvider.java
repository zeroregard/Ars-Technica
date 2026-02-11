package com.zeroregard.ars_technica.saucelib.api.compound;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Marks effects that accept a "subsequent" effect in spell crafting and/or provide
 * extra tooltip lines depending on context. Used for spell crafting UI: highlights valid
 * next glyphs, and can add default vs spell-context tooltip lines (e.g. in grid vs in spell strip).
 * <p>
 */
public interface ISubsequentEffectProvider {

    ResourceLocation[] getSubsequentEffectGlyphs();

    /**
     * True if the given part belongs to this effect's "cluster" (compatible augment or declared subsequent effect).
     * Used by {@link SubsequentContextHelper#getCluster} without needing protected API.
     */
    boolean isPartInCluster(AbstractSpellPart part);

    /**
     * Extra tooltip lines when the glyph is <em>not</em> in a spell (e.g. grid, docs, JEI).
     * Default empty.
     */
    default List<Component> getDefaultAdditionalTooltip() {
        return List.of();
    }

    /**
     * Extra tooltip line when the glyph <em>is</em> in the current spell (e.g. spell strip).
     * Default null (no line added).
     *
     * @param spell          the current spell (list of parts)
     * @param thisGlyphIndex index of this glyph in the spell
     */
    default Component getSpellContextAdditionalTooltip(List<AbstractSpellPart> spell, int thisGlyphIndex) {
        return null;
    }

    default Component createSubsequentGlyphTooltip(ResourceLocation glyphId) {
        String key = getSubsequentGlyphTranslationKey(glyphId);
        return key != null ? Component.translatable(key) : null;
    }

    default String getSubsequentGlyphTranslationKey(ResourceLocation glyphId) {
        ResourceLocation providerId = getProviderId();
        if (providerId == null || glyphId == null) {
            return null;
        }
        String effectPath = sanitize(providerId.getPath());
        String glyphPath = sanitize(glyphId.getPath());

        if (glyphPath.startsWith("glyph_")) {
            glyphPath = glyphPath.substring(6);
        }
        if (glyphPath.endsWith("_glyph")) {
            glyphPath = glyphPath.substring(0, glyphPath.length() - 6);
        }

        return providerId.getNamespace() + ".effect_augment_desc." + effectPath + "_glyph_" + glyphPath;
    }

    private ResourceLocation getProviderId() {
        if (this instanceof AbstractSpellPart spellPart) {
            return spellPart.getRegistryName();
        }
        return null;
    }

    private static String sanitize(String value) {
        return Objects.requireNonNullElse(value, "unknown").toLowerCase(Locale.ROOT);
    }
}
