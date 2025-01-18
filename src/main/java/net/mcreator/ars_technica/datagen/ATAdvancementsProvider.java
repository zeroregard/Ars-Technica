package net.mcreator.ars_technica.datagen;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancementBuilder;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancements;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ATAdvancementsProvider extends ForgeAdvancementProvider {

    public ATAdvancementsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new AEAdvancements()));
    }

    public static class AEAdvancements extends ANAdvancements {

        static Consumer<Advancement> advancementConsumer;

        static Advancement ars_parent(String name) {
            return new Advancement(new ResourceLocation(ArsNouveau.MODID, name), null, null, AdvancementRewards.EMPTY, ImmutableMap.of(), null, false);
        }

        static Advancement technica_parent(String name) {
            return new Advancement(new ResourceLocation(ArsTechnicaMod.MODID, name), null, null, AdvancementRewards.EMPTY, ImmutableMap.of(), null, false);
        }

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<Advancement> con, ExistingFileHelper existingFileHelper) {
            advancementConsumer = con;
            saveBasicItem(ItemsRegistry.CALIBRATED_PRECISION_MECHANISM.get(), ars_parent("enchanting_apparatus"));
            saveBasicItem(ItemsRegistry.SOURCE_ENGINE.get(), technica_parent("calibrated_precision_mechanism"));
            saveBasicItem(ItemsRegistry.RUNIC_SPANNER.get(), technica_parent("calibrated_precision_mechanism"));
            saveBasicItem(ItemsRegistry.SPY_MONOCLE.get(), technica_parent("calibrated_precision_mechanism"));
            saveBasicItem(ItemsRegistry.TRANSMUTATION_FOCUS.get(), technica_parent("calibrated_precision_mechanism"));
        }

        public Advancement saveBasicItem(ItemLike item, Advancement parent) {
            return buildBasicItem(item, ForgeRegistries.ITEMS.getKey(item.asItem()).getPath(), FrameType.TASK, parent).save(advancementConsumer);
        }

        public ANAdvancementBuilder buildBasicItem(ItemLike item, String id, FrameType frame,Advancement parent) {
            return builder(id).display(item, frame).requireItem(item).parent(parent);
        }

        public ANAdvancementBuilder builder(String key) {
            return ANAdvancementBuilder.builder(ArsTechnicaMod.MODID, key);
        }

    }

}