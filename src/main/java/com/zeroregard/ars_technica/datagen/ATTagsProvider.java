package com.zeroregard.ars_technica.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ATTagsProvider {

  public static class ATItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {

    public static final TagKey<Item> MAGIC_HOOD = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "hood"));
    public static final TagKey<Item> MAGIC_ROBE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "robe"));
    public static final TagKey<Item> MAGIC_LEG = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "legs"));
    public static final TagKey<Item> MAGIC_BOOT = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "boot"));
    public static final TagKey<Item> MAGIC_ARMOR = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "magic_armor"));

    public ATItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, Registries.ITEM, lookupProvider, item -> item.builtInRegistryHolder().key(), ArsTechnica.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
      addArmorTags(ItemRegistry.TECHNOMANCER_HELMET.get(), ItemRegistry.TECHNOMANCER_CHESTPLATE.get(), ItemRegistry.TECHNOMANCER_LEGGINGS.get(), ItemRegistry.TECHNOMANCER_BOOTS.get());
      addArmorTags(ItemRegistry.ARTIFICER_CAP.get(), ItemRegistry.ARTIFICER_TUNIC.get(), ItemRegistry.ARTIFICER_PANTS.get(), ItemRegistry.ARTIFICER_SHOES.get());
      addArmorTags(ItemRegistry.MACHINAGUARD_HELMET.get(), ItemRegistry.MACHINAGUARD_CHESTPLATE.get(), ItemRegistry.MACHINAGUARD_LEGGINGS.get(), ItemRegistry.MACHINAGUARD_BOOTS.get());
    }

    private void addArmorTags(Item hat, Item chest, Item legs, Item boots) {
      tag(ItemTags.ARMOR_ENCHANTABLE).add(hat, chest, legs, boots);
      tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(hat, chest, legs, boots);
      tag(ItemTags.DURABILITY_ENCHANTABLE).add(hat, chest, legs, boots);

      tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(hat);
      tag(ItemTags.HEAD_ARMOR).add(hat);
      tag(MAGIC_HOOD).add(hat);
      tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(chest);
      tag(ItemTags.CHEST_ARMOR).add(chest);
      tag(MAGIC_ROBE).add(chest);
      tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(legs);
      tag(ItemTags.LEG_ARMOR).add(legs);
      tag(MAGIC_LEG).add(legs);
      tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(boots);
      tag(ItemTags.FOOT_ARMOR).add(boots);
      tag(MAGIC_BOOT).add(boots);

      tag(MAGIC_ARMOR).add(hat, chest, legs, boots);
    }

    @Override
    public @NotNull String getName() {
      return "Ars Technica Item Tags";
    }
  }

  public static class CItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
    // C namespace tags for common tags
    public static final TagKey<Item> EXPERIENCE_3 = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "consumables/experience_3"));
    public static final TagKey<Item> EXPERIENCE_12 = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "consumables/experience_12"));
    public static final TagKey<Item> EXPERIENCE_48 = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "consumables/experience_48"));
    public static final TagKey<Item> EXPERIENCE_192 = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "consumables/experience_192"));
    public static final TagKey<Item> WRENCH = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tools/wrench"));
    public static final TagKey<Item> MUSIC_DISCS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "music_discs"));
    public static final TagKey<Item> BLANK_MUSIC_DISC = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "blank_music_disc"));

    public CItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, Registries.ITEM, lookupProvider, item -> item.builtInRegistryHolder().key(), "c", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
      tag(EXPERIENCE_3)
          .replace(false)
          .addOptional(ResourceLocation.fromNamespaceAndPath("ars_nouveau", "experience_gem"))
          .addOptional(ResourceLocation.fromNamespaceAndPath("create", "experience_nugget"));
      
      tag(EXPERIENCE_12)
          .replace(false)
          .addOptional(ResourceLocation.fromNamespaceAndPath("ars_nouveau", "greater_experience_gem"));
      
      tag(EXPERIENCE_48)
          .replace(false)
          .add(ItemRegistry.GIANT_EXPERIENCE_GEM.get());
      
      tag(EXPERIENCE_192)
          .replace(false)
          .add(ItemRegistry.GARGANTUAN_EXPERIENCE_GEM.get());

      // Tool tags - needs replace: false
      tag(WRENCH)
          .replace(false)
          .add(ItemRegistry.RUNIC_SPANNER.get());

      // Music disc tag - include our pocket factory disc
      tag(MUSIC_DISCS)
          .replace(false)
          .add(ItemRegistry.POCKET_FACTORY.get());

      // Blank music disc tag - include both our blank disc and etched's blank disc
      tag(BLANK_MUSIC_DISC)
          .replace(false)
          .add(ItemRegistry.BLANK_DISC.get())
          .addOptional(ResourceLocation.fromNamespaceAndPath("etched", "blank_music_disc"));
    }

    @Override
    public @NotNull String getName() {
      return "C Item Tags";
    }
  }

  public static class CuriosItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
    // Curios namespace tags
    public static final TagKey<Item> AN_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "an_focus"));
    public static final TagKey<Item> HEAD = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "head"));

    public CuriosItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, Registries.ITEM, lookupProvider, item -> item.builtInRegistryHolder().key(), "curios", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
      tag(AN_FOCUS)
          .replace(false)
          .add(ItemRegistry.TRANSMUTATION_FOCUS.get());
      
      tag(HEAD)
          .add(ItemRegistry.SPY_MONOCLE.get());
    }

    @Override
    public @NotNull String getName() {
      return "Curios Item Tags";
    }
  }
}