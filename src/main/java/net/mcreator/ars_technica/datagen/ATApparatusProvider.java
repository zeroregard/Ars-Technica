package net.mcreator.ars_technica.datagen;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;

import net.mcreator.ars_technica.recipe.TechnomancerArmorRecipe;
import net.mcreator.ars_technica.setup.ArsElementalModItems;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static com.simibubi.create.AllItems.BRASS_INGOT;
import static com.simibubi.create.AllItems.GOGGLES;
import static com.simibubi.create.AllItems.PRECISION_MECHANISM;

import java.nio.file.Path;

public class ATApparatusProvider extends ApparatusRecipeProvider {

  public ATApparatusProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  public void collectJsons(CachedOutput cache) {
    addTechnomancerArmorRecipes();
    addCurioRecipes();
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
              .withReagent(com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.MANIPULATION_ESSENCE)
              .withPedestalItem(1, Ingredient.of(BRASS_INGOT))
              .withPedestalItem(Items.PISTON)
              .withPedestalItem(PRECISION_MECHANISM)
              .withPedestalItem(Ingredient.of(Items.DIAMOND))
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