package com.zeroregard.ars_technica.client.events;

import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.item.SpyMonocle;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber(modid = ArsTechnica.MODID, value = Dist.CLIENT)
public class SpyMonocleEvents {

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        CuriosApi.getCuriosInventory(player).flatMap(handler -> handler.findFirstCurio(ItemRegistry.SPY_MONOCLE.get())).ifPresent(slotResult -> {
            ItemStack stack = slotResult.stack();
            if (SpyMonocle.isZoomed(stack)) {
                float newFov = event.getFovModifier() * SpyMonocle.ZOOM_FOV_MODIFIER;
                event.setNewFovModifier(newFov);
            }
        });
    }
}