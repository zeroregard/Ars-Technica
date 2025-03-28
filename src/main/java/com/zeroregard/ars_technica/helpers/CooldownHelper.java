package com.zeroregard.ars_technica.helpers;

public class CooldownHelper {
    public static String getCooldownText(int ticksValue) {
        if (ticksValue == 0) {
            return "0";
        }
        String text = "";
        var seconds = (int) Math.floor(ticksValue / 20.0);
        if (seconds > 0) {
            text = String.format("%ds", seconds);
        }
        var remainingTicks = ticksValue % 20;
        if(seconds > 0 && remainingTicks > 0) {
            text += " ";
        }
        if(remainingTicks != 0) {
            text += String.format("%dt", remainingTicks);
        }
        return text;
    }
}
