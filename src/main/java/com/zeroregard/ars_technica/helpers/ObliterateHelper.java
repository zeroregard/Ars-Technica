package com.zeroregard.ars_technica.helpers;

import com.zeroregard.ars_technica.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ObliterateHelper {
    private static final int MAX_FORTUNE_DISPLAY_LEVEL = 4;

    private ObliterateHelper() {
    }

    public static float getFortuneChanceMultiplier(int fortuneLevel) {
        if (fortuneLevel <= 0) {
            return 1.0f;
        }
        double base = Config.Common.OBLITERATE_FORTUNE_BASE_CHANCE.get();
        double multiplier = 1.0 + (2.0 * base * (1.0 - Math.pow(0.5, fortuneLevel)));
        return (float) multiplier;
    }

    public static boolean isChanceCapped(float baseChance) {
        return baseChance >= 1.0f;
    }

    public static Component createHoldPrompt(Component keyName) {
        return Component.translatable("tooltip.ars_technica.obliterate.hold", keyName.copy().withStyle(ChatFormatting.AQUA))
                .withStyle(ChatFormatting.GRAY);
    }

    public static List<Component> createFortuneDetails(float baseChance) {
        List<Component> lines = new ArrayList<>();
        float basePercent = Math.min(baseChance, 1.0f) * 100.0f;
        String baseValue = String.format(Locale.ROOT, "%.2f%% Chance", basePercent);
        lines.add(Component.literal("Base: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(baseValue).withStyle(ChatFormatting.GOLD)));

        for (int level = 1; level <= MAX_FORTUNE_DISPLAY_LEVEL; level++) {
            float chance = getFortuneChanceMultiplier(level) * baseChance;
            chance = Math.min(chance, 1.0f);
            String value = String.format(Locale.ROOT, "%.2f%% Chance", chance * 100.0f);
            Component line = Component.literal("+%d Luck: ".formatted(level)).withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(value).withStyle(ChatFormatting.GOLD));
            lines.add(line);
        }
        return lines;
    }
}


