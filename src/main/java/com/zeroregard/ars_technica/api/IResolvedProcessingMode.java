package com.zeroregard.ars_technica.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Ars Technica Create-style processing glyphs can implement this to describe the
 * <em>resolved</em> processing mode when this glyph is already in the current spell.
 * Used only when the tooltipped glyph is part of the current spell (spell strip or picker).
 */
public interface IResolvedProcessingMode {

    /**
     * Returns a single tooltip line for the processing mode that will actually run
     * for this spell (e.g. "Heated Compacting - Fluid aware!").
     *
     * @param spell          the current spell (list of parts)
     * @param thisGlyphIndex index of this glyph in the spell
     */
    Component getActiveProcessingTypeTooltip(List<AbstractSpellPart> spell, int thisGlyphIndex);
}
