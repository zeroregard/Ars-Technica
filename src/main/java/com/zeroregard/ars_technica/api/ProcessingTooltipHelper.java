package com.zeroregard.ars_technica.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.zeroregard.ars_technica.saucelib.api.compound.ISubsequentEffectProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Single place for processing-mode tooltip logic: in the spell strip show the active recipe in green;
 * in the grid (or items/docs) show possible processing only when shift is held, in green.
 * Uses {@link ISubsequentEffectProvider#getDefaultAdditionalTooltip} and
 * {@link ISubsequentEffectProvider#getSpellContextAdditionalTooltip}.
 */
public final class ProcessingTooltipHelper {

    private ProcessingTooltipHelper() {}

    /**
     * Appends processing tooltip lines when the part implements {@link ISubsequentEffectProvider}.
     * When {@code partIsInSpellStrip}, adds one line from {@link ISubsequentEffectProvider#getSpellContextAdditionalTooltip}
     * (e.g. "[Create Recipe: Press]") in green. Otherwise, if {@code shiftDown}, adds one line from
     * {@link ISubsequentEffectProvider#getDefaultAdditionalTooltip} (e.g. "(Create Processing: Press, Compact, Pack)") in green.
     */
    public static void addProcessingTooltipLines(
            AbstractSpellPart part,
            List<Component> lines,
            boolean shiftDown,
            boolean partIsInSpellStrip,
            List<AbstractSpellPart> currentSpell,
            int partIndexInSpell) {
        if (part == null || !(part instanceof ISubsequentEffectProvider provider)) return;

        if (partIsInSpellStrip
                && currentSpell != null && !currentSpell.isEmpty()
                && partIndexInSpell >= 0 && partIndexInSpell < currentSpell.size()) {
            Component active = provider.getSpellContextAdditionalTooltip(currentSpell, partIndexInSpell);
            if (active != null) {
                lines.add(Component.literal("[Create Recipe: " + active.getString() + "]")
                        .withStyle(ChatFormatting.GREEN));
            }
            return;
        }

        if (shiftDown) {
            List<Component> modeLines = provider.getDefaultAdditionalTooltip();
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
