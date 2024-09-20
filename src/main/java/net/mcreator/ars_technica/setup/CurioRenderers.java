package net.mcreator.ars_technica.setup;

import net.mcreator.ars_technica.common.items.equipment.SpyMonocleCurioRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@OnlyIn(Dist.CLIENT)
public class CurioRenderers {
    public static void register() {
        CuriosRendererRegistry.register(ItemsRegistry.SPY_MONOCLE.get(), () -> new SpyMonocleCurioRenderer(Minecraft.getInstance().getEntityModels().bakeLayer(SpyMonocleCurioRenderer.LAYER)));
    }

    public static void onLayerRegister(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpyMonocleCurioRenderer.LAYER, () -> LayerDefinition.create(SpyMonocleCurioRenderer.mesh(), 1, 1));
    }
}