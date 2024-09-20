package net.mcreator.ars_technica.datagen;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import net.mcreator.ars_technica.common.items.threads.PressurePerk;
import net.mcreator.ars_technica.recipe.TechnomancerArmorRecipe;
import net.mcreator.ars_technica.setup.ArsElementalModItems;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.*;
import static com.simibubi.create.AllItems.BRASS_INGOT;
import static com.simibubi.create.AllItems.GOGGLES;
import static com.simibubi.create.AllItems.PRECISION_MECHANISM;
import static com.simibubi.create.AllItems.WRENCH;
import static com.simibubi.create.AllItems.COPPER_BACKTANK;

import java.nio.file.Path;

public class ATApparatusProvider extends ApparatusRecipeProvider {

  public ATApparatusProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  public void collectJsons(CachedOutput cache) {
    addTechnomancerArmorRecipes();
    addCurioRecipes();
    addIngredientRecipes();
    addEquipmentRecipes();
    addThreadRecipes();
    Path output = this.generator.getPackOutput().getOutputFolder();
    for (EnchantingApparatusRecipe g : recipes) {
      if (g != null) {
        Path path = getRecipePath(output, g.getId().getPath());
        saveStable(cache, g.asRecipe(), path);
      }
    }
  }

  protected void addTechnomancerArmorRecipes() {
    recipes.add(new TechnomancerArmorRecipe(
        builder().withResult(ItemsRegistry.TECHNOMANCER_HELMET.get())
            .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_HOOD))
            .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get()).withPedestalItem(Items.NETHERITE_INGOT)
            .withPedestalItem(BRASS_INGOT).withPedestalItem(GOGGLES).withSourceCost(7000).keepNbtOfReagent(true).build()));
    recipes.add(new TechnomancerArmorRecipe(builder().withResult(ItemsRegistry.TECHNOMANCER_CHESTPLATE.get())
        .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_ROBE))
        .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get()).withPedestalItem(Items.NETHERITE_INGOT)
        .withPedestalItem(2, BRASS_INGOT).withSourceCost(7000).keepNbtOfReagent(true).build()));
    recipes.add(new TechnomancerArmorRecipe(
        builder().withResult((ItemsRegistry.TECHNOMANCER_LEGGINGS.get())).withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_LEG))
            .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get()).withPedestalItem(Items.NETHERITE_INGOT)
            .withPedestalItem(2, BRASS_INGOT).withSourceCost(7000).keepNbtOfReagent(true).build()));
    recipes.add(new TechnomancerArmorRecipe(builder().withResult(ItemsRegistry.TECHNOMANCER_BOOTS.get())
        .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_BOOT))
        .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get()).withPedestalItem(Items.NETHERITE_INGOT)
        .withPedestalItem(2, BRASS_INGOT).withSourceCost(7000).keepNbtOfReagent(true).build()));
  }


  protected void addCurioRecipes() {
    recipes.add(builder().withResult(ItemsRegistry.TRANSMUTATION_FOCUS)
              .withReagent(MANIPULATION_ESSENCE)
              .withPedestalItem(1, Ingredient.of(BRASS_INGOT))
              .withPedestalItem(Items.RABBIT_FOOT)
              .withPedestalItem(PRECISION_MECHANISM)
              .withPedestalItem(Ingredient.of(Items.EMERALD))
              .build());
  }

  protected void addIngredientRecipes() {
    recipes.add(builder().withResult(ItemsRegistry.CALIBRATED_PRECISION_MECHANISM)
            .withReagent(PRECISION_MECHANISM)
            .withPedestalItem(4, Ingredient.of(Items.AMETHYST_SHARD))
            .withPedestalItem(4, Ingredient.of(SOURCE_GEM))
            .withSourceCost(500)
            .build());
  }

  protected void addEquipmentRecipes() {
    recipes.add(builder().withResult(ItemsRegistry.RUNIC_SPANNER)
            .withReagent(WRENCH)
            .withPedestalItem(Ingredient.of(Items.GOLD_INGOT))
            .withPedestalItem(Ingredient.of(new ItemStack(ItemsRegistry.CALIBRATED_PRECISION_MECHANISM.get())))
            .withPedestalItem(Ingredient.of(MANIPULATION_ESSENCE))
            .withSourceCost(500)
            .build());

    recipes.add(builder().withResult(ItemsRegistry.SPY_MONOCLE)
            .withReagent(Items.SPYGLASS)
            .withPedestalItem(Ingredient.of(Items.GOLD_INGOT))
            .withPedestalItem(Ingredient.of(new ItemStack(ItemsRegistry.CALIBRATED_PRECISION_MECHANISM.get())))
            .withPedestalItem(Ingredient.of(MANIPULATION_ESSENCE))
            .withSourceCost(500)
            .build());
  }

  protected void addThreadRecipes() {
    recipes.add(builder()
            .withResult(getPerkItem(PressurePerk.INSTANCE.getRegistryName()))
            .withReagent(BLANK_THREAD)
            .withPedestalItem(3, Ingredient.of(AIR_ESSENCE))
            .withPedestalItem(Ingredient.of(COPPER_BACKTANK))
            .withPedestalItem(Ingredient.of(new ItemStack(ItemsRegistry.CALIBRATED_PRECISION_MECHANISM.get())))
            .build());
  }

  protected static Path getRecipePath(Path pathIn, String str) {
    return pathIn.resolve("data/ars_technica/recipes/" + str + ".json");
  }

  @Override
  public String getName() {
    return "Ars Technica Apparatus";
  }
}