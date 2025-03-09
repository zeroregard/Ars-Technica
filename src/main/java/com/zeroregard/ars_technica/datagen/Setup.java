package com.zeroregard.ars_technica.datagen;

import com.zeroregard.ars_technica.ArsTechnica;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ArsTechnica.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Setup {
    public static CompletableFuture<HolderLookup.Provider> provider;
    public static PackOutput output;

    //use runData configuration to generate stuff, event.includeServer() for data, event.includeClient() for assets
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        provider = event.getLookupProvider();
        output = gen.getPackOutput();

        gen.addProvider(event.includeServer(), new ArsProviders.EnchantingAppProvider(gen));
        // gen.addProvider(event.includeServer(), new ArsProviders.ImbuementProvider(gen));


        // gen.addProvider(event.includeServer(), new ArsProviders.GlyphProvider(gen));
        // gen.addProvider(event.includeServer(), new ArsProviders.PatchouliProvider(gen));
    }

}
