package com.zeroregard.ars_technica.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Single place for processing-mode tooltip logic: in the spell strip show the active recipe in green;
 * in the grid (or items/docs) show possible processing only when shift is held, in green.
 */
public final class ProcessingTooltipHelper {

    private ProcessingTooltipHelper() {}

    /**
     * Appends processing tooltip lines if the part implements the relevant interfaces.
     * When {@code partIsInSpellStrip} is true and the part implements {@link IResolvedProcessingMode},
     * adds one line: "[Create Recipe: {mode}]" in green. Otherwise, if the part implements
     * {@link IPossibleProcessingModes} and {@code shiftDown}, adds one line "(Create Processing: A, B, C)" in green.
     *
     * @param part                the glyph part (may be null)
     * @param lines               tooltip list to append to
     * @param shiftDown           whether shift is held (needed to show possible processing)
     * @param partIsInSpellStrip  whether the hovered control is a spell-strip slot
     * @param currentSpell       the current spell (only used when partIsInSpellStrip is true)
     * @param partIndexInSpell   index of part in currentSpell (only used when partIsInSpellStrip is true)
     */
    public static void addProcessingTooltipLines(
            AbstractSpellPart part,
            List<Component> lines,
            boolean shiftDown,
            boolean partIsInSpellStrip,
            List<AbstractSpellPart> currentSpell,
            int partIndexInSpell) {
        if (part == null) return;

        if (partIsInSpellStrip && part instanceof IResolvedProcessingMode resolved
                && currentSpell != null && !currentSpell.isEmpty()
                && partIndexInSpell >= 0 && partIndexInSpell < currentSpell.size()) {
            Component active = resolved.getActiveProcessingTypeTooltip(currentSpell, partIndexInSpell);
            lines.add(Component.literal("[Create Recipe: " + active.getString() + "]")
                    .withStyle(ChatFormatting.GREEN));
            return;
        }

        if (shiftDown && part instanceof IPossibleProcessingModes possible) {
            List<Component> modeLines = possible.getPossibleProcessingTypesTooltip();
            if (!modeLines.isEmpty()) {
                String joined = modeLines.stream()
                        .map(Component::getString)
                        .collect(Collectors.joining(", "));
                lines.add(Component.literal("(Create Processing: " + joined + ")")
                        .withStyle(ChatFormatting.GREEN));
            }
        }
    }
}
