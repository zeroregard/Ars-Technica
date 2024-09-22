package net.mcreator.ars_technica.client.events;

import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.ars_technica.common.items.equipment.SpyMonocle;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = "ars_technica", value = Dist.CLIENT)
public class SpyMonocleEvents {

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        CuriosApi.getCuriosHelper().findEquippedCurio(ItemsRegistry.SPY_MONOCLE.get(), player).ifPresent(pair -> {
            ItemStack stack = pair.right;
            if (SpyMonocle.isZoomed(stack)) {
                float newFov = event.getFovModifier() * SpyMonocle.ZOOM_FOV_MODIFIER;
                event.setNewFovModifier(newFov);
            }
        });
    }
}