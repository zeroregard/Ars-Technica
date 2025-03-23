package com.zeroregard.ars_technica.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.CompletableFuture;

public class ATTagsProvider {

  public static class ATItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
    private static final Logger LOGGER = ArsTechnica.LOGGER;
    String[] curioSlots = {"curio", "back", "belt", "body", "bracelet", "charm", "feet", "head", "hands", "necklace", "ring", "spellbook"};

    static TagKey<Item> curiosTag(String key) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CuriosApi.MODID, key));
    }

    public static final TagKey<Item> MAGIC_HOOD = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "hood"));
    public static final TagKey<Item> MAGIC_ROBE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "robe"));
    public static final TagKey<Item> MAGIC_LEG = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "legs"));
    public static final TagKey<Item> MAGIC_BOOT = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "boot"));


    public ATItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
      super(output, Registries.ITEM, future, item -> item.builtInRegistryHolder().key(), ArsTechnica.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
      tag(MAGIC_HOOD).add(ItemRegistry.TECHNOMANCER_HELMET.get());
      tag(MAGIC_ROBE).add(ItemRegistry.TECHNOMANCER_CHESTPLATE.get());
      tag(MAGIC_LEG).add(ItemRegistry.TECHNOMANCER_LEGGINGS.get());
      tag(MAGIC_BOOT).add(ItemRegistry.TECHNOMANCER_BOOTS.get());
    }

    @Override
    public @NotNull String getName() {
      return "Ars Technica Item Tags";
    }
  }
}