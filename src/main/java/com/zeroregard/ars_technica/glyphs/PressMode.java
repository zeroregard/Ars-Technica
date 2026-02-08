package com.zeroregard.ars_technica.glyphs;

import net.minecraft.network.chat.Component;

/**
 * Resolved processing mode for the Press glyph (used for tooltips and cluster interpretation).
 */
public enum PressMode {
    PRESSING("pressing", "Press"),
    PACKING("packing", "Pack"),
    COMPACTING("compacting", "Compact"),
    HEATED_COMPACTING("heated_compacting", "Heated Compact"),
    SUPERHEATED_COMPACTING("superheated_compacting", "Superheated Compact");

    private final String translationKey;
    /** Short name for "[Create Recipe: X]" tooltip. */
    private final String recipeDisplayName;

    PressMode(String path, String recipeDisplayName) {
        this.translationKey = "ars_technica.tooltip.press_mode." + path;
        this.recipeDisplayName = recipeDisplayName;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Component getComponent() {
        return Component.translatable(translationKey);
    }

    /** Short name used in "[Create Recipe: X]" on the spell strip. */
    public String getRecipeDisplayName() {
        return recipeDisplayName;
    }
}
