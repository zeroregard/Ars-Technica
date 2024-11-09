package net.mcreator.ars_technica.datagen;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.mcreator.ars_technica.ConfigHandler;
import net.mcreator.ars_technica.common.items.threads.PressurePerk;
import net.mcreator.ars_technica.recipe.TechnomancerArmorRecipe;
import net.mcreator.ars_technica.setup.ArsElementalModItems;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.*;
import static com.simibubi.create.AllItems.GOGGLES;
import static com.simibubi.create.AllItems.PRECISION_MECHANISM;
import static com.simibubi.create.AllItems.WRENCH;
import static com.simibubi.create.AllItems.COPPER_BACKTANK;

import java.nio.file.Path;

public class ATApparatusProvider extends ApparatusRecipeProvider {
    private static final TagKey<Item> BRASS_INGOT_TAG = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("forge", "ingots/brass"));
    public static final Ingredient BRASS_INGOT = Ingredient.of(BRASS_INGOT_TAG);
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

    private ConditionalApparatusRecipeBuilder conditionalBuilder() {
        return ConditionalApparatusRecipeBuilder.conditionalBuilder();
    }


    protected void addTechnomancerArmorRecipes() {
        recipes.add(new TechnomancerArmorRecipe(
                conditionalBuilder()
                        .withResult(ItemsRegistry.TECHNOMANCER_HELMET.get())
                        .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_HOOD))
                        .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get())
                        .withPedestalItem(Items.NETHERITE_INGOT)
                        .withPedestalItem(BRASS_INGOT)
                        .withPedestalItem(GOGGLES)
                        .withSourceCost(7000)
                        .keepNbtOfReagent(true)
                        .build()));
        recipes.add(new TechnomancerArmorRecipe(
                conditionalBuilder()
                        .withResult(ItemsRegistry.TECHNOMANCER_CHESTPLATE.get())
                        .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_ROBE))
                        .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get())
                        .withPedestalItem(Items.NETHERITE_INGOT)
                        .withPedestalItem(2, BRASS_INGOT)
                        .withSourceCost(7000)
                        .keepNbtOfReagent(true)
                        .build()));
        recipes.add(new TechnomancerArmorRecipe(
                conditionalBuilder()
                        .withResult((ItemsRegistry.TECHNOMANCER_LEGGINGS.get()))
                        .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_LEG))
                        .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get())
                        .withPedestalItem(Items.NETHERITE_INGOT)
                        .withPedestalItem(2, BRASS_INGOT)
                        .withSourceCost(7000)
                        .keepNbtOfReagent(true)
                        .build()));
        recipes.add(new TechnomancerArmorRecipe(
                conditionalBuilder()
                        .withResult(ItemsRegistry.TECHNOMANCER_BOOTS.get())
                        .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_BOOT))
                        .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get())
                        .withPedestalItem(Items.NETHERITE_INGOT)
                        .withPedestalItem(2, BRASS_INGOT)
                        .withSourceCost(7000)
                        .keepNbtOfReagent(true)
                        .build()));
    }


    protected void addCurioRecipes() {
        recipes.add(conditionalBuilder()
                .withConfigCondition(ConfigHandler.Common.RECIPE_FOCUS_TRANSMUTATION_CONFIG_OPTION)
                .withResult(ItemsRegistry.TRANSMUTATION_FOCUS)
                .withReagent(MANIPULATION_ESSENCE)
                .withPedestalItem(1, BRASS_INGOT)
                .withPedestalItem(Items.RABBIT_FOOT)
                .withPedestalItem(ItemsRegistry.CALIBRATED_PRECISION_MECHANISM)
                .withPedestalItem(Ingredient.of(Items.EMERALD))
                .build());
    }

    protected void addIngredientRecipes() {
        recipes.add(conditionalBuilder()
                .withConfigCondition(ConfigHandler.Common.RECIPE_CALIBRATED_PRECISION_MECHANISM_CONFIG_OPTION)
                .withResult(ItemsRegistry.CALIBRATED_PRECISION_MECHANISM)
                .withReagent(PRECISION_MECHANISM)
                .withPedestalItem(4, Ingredient.of(Items.AMETHYST_SHARD))
                .withPedestalItem(4, Ingredient.of(SOURCE_GEM))
                .withSourceCost(500)
                .build());
    }

    protected void addEquipmentRecipes() {
        recipes.add(conditionalBuilder()
                .withConfigCondition(ConfigHandler.Common.RECIPE_RUNIC_SPANNER_CONFIG_OPTION)
                .withResult(ItemsRegistry.RUNIC_SPANNER)
                .withReagent(WRENCH)
                .withPedestalItem(Ingredient.of(Items.GOLD_INGOT))
                .withPedestalItem(Ingredient.of(new ItemStack(ItemsRegistry.CALIBRATED_PRECISION_MECHANISM.get())))
                .withPedestalItem(Ingredient.of(MANIPULATION_ESSENCE))
                .withSourceCost(500)
                .build());

        recipes.add(conditionalBuilder()
                .withConfigCondition(ConfigHandler.Common.RECIPE_SPY_MONOCLE_CONFIG_OPTION)
                .withResult(ItemsRegistry.SPY_MONOCLE)
                .withReagent(Items.SPYGLASS)
                .withPedestalItem(Ingredient.of(new ItemStack(ItemsRegistry.CALIBRATED_PRECISION_MECHANISM.get())))
                .build());
    }

    protected void addThreadRecipes() {
        recipes.add(conditionalBuilder()
                .withConfigCondition(ConfigHandler.Common.RECIPE_THREAD_PRESSURE_CONFIG_OPTION)
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