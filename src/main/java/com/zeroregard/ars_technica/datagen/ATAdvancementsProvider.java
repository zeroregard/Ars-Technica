package com.zeroregard.ars_technica.datagen;

import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancementBuilder;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancements;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.advancements.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;


public class ATAdvancementsProvider extends net.neoforged.neoforge.common.data.AdvancementProvider  {

    public ATAdvancementsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new ATAdvancements()));
    }

    public static class ATAdvancements extends ANAdvancements {

        static Consumer<AdvancementHolder> advancementConsumer;

        static ResourceLocation ars_parent(String name) {
            return com.hollingsworth.arsnouveau.ArsNouveau.prefix(name);

        }

        static ResourceLocation technica_parent(String name) {
            return prefix(name);
        }

        @Override
        public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> con, @NotNull ExistingFileHelper existingFileHelper) {
            advancementConsumer = con;
            saveBasicItem(ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get(), ars_parent("enchanting_apparatus"));
            saveBasicItem(ItemRegistry.SOURCE_MOTOR.get(), technica_parent("calibrated_precision_mechanism"));
            saveBasicItem(ItemRegistry.RUNIC_SPANNER.get(), technica_parent("calibrated_precision_mechanism"));
            saveBasicItem(ItemRegistry.SPY_MONOCLE.get(), technica_parent("calibrated_precision_mechanism"));
            saveBasicItem(ItemRegistry.TRANSMUTATION_FOCUS.get(), technica_parent("calibrated_precision_mechanism"));
        }

        public AdvancementHolder saveBasicItem(ItemLike item, ResourceLocation parent) {
            return buildBasicItem(item, BuiltInRegistries.ITEM.getKey(item.asItem()).getPath(), AdvancementType.TASK, parent).save(advancementConsumer);
        }

        public ANAdvancementBuilder buildBasicItem(ItemLike item, String id, AdvancementType type, ResourceLocation parent) {
            return builder(id).display(item, type).requireItem(item).parent(parent);
        }

        public ANAdvancementBuilder builder(String key) {
            return ANAdvancementBuilder.builder(ArsTechnica.MODID, key);
        }

    }

}