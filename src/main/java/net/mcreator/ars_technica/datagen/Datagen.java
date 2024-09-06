package net.mcreator.ars_technica.datagen;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

import static net.mcreator.ars_technica.ArsTechnicaMod.LOGGER;


@Mod.EventBusSubscriber(modid = ArsTechnicaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        BlockTagsProvider BTP = new ATTagsProvider.ATBlockTagsProvider(gen, provider, existingFileHelper);
        gen.addProvider(event.includeServer(), BTP);
        gen.addProvider(event.includeServer(), new ATTagsProvider.ATItemTagsProvider(gen, provider, BTP, existingFileHelper));
        LOGGER.info("Tags done, now for apparatus");
        gen.addProvider(event.includeServer(), new ATApparatusProvider(gen));
        LOGGER.info("Apparatus done");

    }

}