package com.zeroregard.ars_technica.api;

import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Ars Technica Create-style processing glyphs can implement this to describe their
 * <em>possible</em> processing modes (what this glyph can do).
 * Used in the glyph building area, Documentation, and JEI when the glyph
 * is not part of the current spell.
 */
public interface IPossibleProcessingModes {

    /**
     * Returns tooltip lines for all processing modes this glyph can produce
     * (e.g. Press: Pressing, Packing, Compacting, Heated Compacting, Superheated Compacting).
     */
    List<Component> getPossibleProcessingTypesTooltip();
}
