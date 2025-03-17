package com.zeroregard.ars_technica;

import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import com.zeroregard.ars_technica.client.block.AllPartialModels;
import com.zeroregard.ars_technica.ponder.ATPonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

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
    }
}
