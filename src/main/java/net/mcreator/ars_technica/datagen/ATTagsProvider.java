package net.mcreator.ars_technica.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.Logger;

public class ATTagsProvider {

  public static class ATItemTagsProvider extends ItemTagsProvider {
    private static final Logger LOGGER = ArsTechnicaMod.LOGGER;
    public static final TagKey<Item> MAGIC_HOOD = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "hood"));
    public static final TagKey<Item> MAGIC_ROBE = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "robe"));
    public static final TagKey<Item> MAGIC_LEG = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "legs"));
    public static final TagKey<Item> MAGIC_BOOT = ItemTags.create(new ResourceLocation(ArsNouveau.MODID, "boot"));

    public ATItemTagsProvider(DataGenerator gen, CompletableFuture<HolderLookup.Provider> provider,
                              BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(gen.getPackOutput(), provider, blockTagsProvider.contentsGetter(), ArsTechnicaMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
      tag(MAGIC_HOOD).add(ItemsRegistry.BATTLEMAGE_HOOD.get(), ItemsRegistry.ARCANIST_HOOD.get(),
              ItemsRegistry.SORCERER_HOOD.get());
      tag(MAGIC_ROBE).add(ItemsRegistry.BATTLEMAGE_ROBES.get(), ItemsRegistry.ARCANIST_ROBES.get(),
              ItemsRegistry.SORCERER_ROBES.get());
      tag(MAGIC_LEG).add(ItemsRegistry.BATTLEMAGE_LEGGINGS.get(), ItemsRegistry.ARCANIST_LEGGINGS.get(),
              ItemsRegistry.SORCERER_LEGGINGS.get());
      tag(MAGIC_BOOT).add(ItemsRegistry.BATTLEMAGE_BOOTS.get(), ItemsRegistry.ARCANIST_BOOTS.get(),
              ItemsRegistry.SORCERER_BOOTS.get());
    }

    @Override
    public @NotNull String getName() {
      return "Ars Technica Item Tags";
    }
  }

  public static class ATBlockTagsProvider extends BlockTagsProvider {

    public ATBlockTagsProvider(DataGenerator gen, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
      super(gen.getPackOutput(), provider, ArsTechnicaMod.MODID, existingFileHelper);
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
}