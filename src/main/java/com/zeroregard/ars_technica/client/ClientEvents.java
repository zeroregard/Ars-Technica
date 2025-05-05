package com.zeroregard.ars_technica.client;
import com.zeroregard.ars_technica.client.item.RunicSpannerRadialWrenchHandler;

import net.minecraft.client.Minecraft;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;


@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        if (!isGameActive())
            return;

        RunicSpannerRadialWrenchHandler.clientTick();
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }


    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        onKeyScreenEvent(event);
    }

    private static void onKeyScreenEvent(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null)
            return;

        int key = event.getKey();
        boolean pressed = !(event.getAction() == 0);

        RunicSpannerRadialWrenchHandler.onKeyInput(key, pressed);
    }


}
