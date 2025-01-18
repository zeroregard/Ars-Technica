package net.mcreator.ars_technica.datagen;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;


@Mod.EventBusSubscriber(modid = ArsTechnicaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        BlockTagsProvider BTP = new ATTagsProvider.ATBlockTagsProvider(gen, provider, existingFileHelper);
        PackOutput output = gen.getPackOutput();

        gen.addProvider(event.includeServer(), BTP);
        gen.addProvider(event.includeServer(), new ATTagsProvider.ATItemTagsProvider(gen, provider, BTP, existingFileHelper));
        gen.addProvider(event.includeServer(), new ATApparatusProvider(gen));
        gen.addProvider(event.includeClient(), new ModBlockStateProvider(gen.getPackOutput(), ArsTechnicaMod.MODID, existingFileHelper));
        gen.addProvider(event.includeServer(), new ATPatchouliProvider(gen));
        gen.addProvider(event.includeServer(), new ATAdvancementsProvider(output, provider, existingFileHelper));
    }

}