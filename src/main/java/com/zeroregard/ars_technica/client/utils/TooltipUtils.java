package com.zeroregard.ars_technica.client.utils;
// Taken directly from https://github.com/Alexthw46/Ars-Elemental/blob/1.21/src/main/java/alexthw/ars_elemental/client/TooltipUtils.java

import com.zeroregard.ars_technica.ArsTechnica;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TooltipUtils {

  public static Component getShiftInfoTooltip(String type) {
    Component shift = Component.literal("SHIFT").withStyle(ChatFormatting.AQUA);
    return Component.translatable(ArsTechnica.MODID + "." + type + ".shift_info", shift)
        .withStyle(ChatFormatting.GRAY);
  }

  public static void addOnShift(List<Component> tooltip, Runnable lambda, String type) {
    if (Screen.hasShiftDown()) {
      lambda.run();
    } else {
      tooltip.add(getShiftInfoTooltip(type));
    }
  }

}