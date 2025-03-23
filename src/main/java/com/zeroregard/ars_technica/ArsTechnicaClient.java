package com.zeroregard.ars_technica;

import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import com.zeroregard.ars_technica.client.block.AllPartialModels;
import com.zeroregard.ars_technica.client.item.SpyMonocleCurioRenderer;
import com.zeroregard.ars_technica.ponder.ATPonderPlugin;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(value = ArsTechnica.MODID, dist = Dist.CLIENT)
public class ArsTechnicaClient {

    public ArsTechnicaClient(net.neoforged.bus.api.IEventBus modEventBus) {
        onCtorClient(modEventBus);
    }


    public static void onCtorClient(net.neoforged.bus.api.IEventBus modEventBus) {
        modEventBus.addListener(ArsTechnicaClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        AllPartialModels.init();
        PonderIndex.addPlugin(new ATPonderPlugin());
        CuriosRendererRegistry.register(ItemRegistry.SPY_MONOCLE.get(), () -> new SpyMonocleCurioRenderer(Minecraft.getInstance().getEntityModels().bakeLayer(SpyMonocleCurioRenderer.SPY_MONOCLE_LAYER)));
    }

}
