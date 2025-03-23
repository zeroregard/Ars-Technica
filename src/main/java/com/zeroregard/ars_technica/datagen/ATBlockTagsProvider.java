package com.zeroregard.ars_technica.datagen;

import com.zeroregard.ars_technica.ArsTechnica;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ATBlockTagsProvider extends BlockTagsProvider {

    public ATBlockTagsProvider(DataGenerator gen, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(gen.getPackOutput(), provider, ArsTechnica.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        // This class only exists to satisfy the requirement for ATItemTagsProvider.
    }

    @Override
    public @NotNull String getName() {
        return "Ars Technica Block Tags";
    }
}