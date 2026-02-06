package com.zeroregard.ars_technica.client;
import com.zeroregard.ars_technica.item.SpyMonocle;
import com.zeroregard.ars_technica.registry.DataComponentRegistry;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentPatch;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (event.getKey() == GLFW.GLFW_KEY_ESCAPE && event.getAction() == GLFW.GLFW_PRESS) {
            CuriosApi.getCuriosInventory(mc.player).flatMap(handler -> handler.findFirstCurio(ItemRegistry.SPY_MONOCLE.get())).ifPresent(slotResult -> {
                var stack = slotResult.stack();
                if (SpyMonocle.isZoomed(stack)) {
                    DataComponentPatch patch = DataComponentPatch.builder()
                            .set(DataComponentRegistry.ZOOMED.get(), false)
                            .build();
                    stack.applyComponents(patch);
                    SpyMonocle.forceResetZoom(mc.player);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        CuriosApi.getCuriosInventory(mc.player).flatMap(handler -> handler.findFirstCurio(ItemRegistry.SPY_MONOCLE.get())).ifPresent(slotResult -> {
            var stack = slotResult.stack();
            if (SpyMonocle.isZoomed(stack)) {
                DataComponentPatch patch = DataComponentPatch.builder()
                        .set(DataComponentRegistry.ZOOMED.get(), false)
                        .build();
                stack.applyComponents(patch);
                SpyMonocle.forceResetZoom(mc.player);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        SpyMonocle.forceResetZoom(null);
    }

}
