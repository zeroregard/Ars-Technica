package com.zeroregard.ars_technica.datagen;


import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.common.datagen.ImbuementRecipeProvider;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.item.PressurePerk;
import com.zeroregard.ars_technica.recipe.TechnomancerArmorRecipe;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.*;
import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.AIR_ESSENCE;
import static com.simibubi.create.AllItems.*;
import static com.zeroregard.ars_technica.datagen.Setup.provider;

public class ArsProviders {

    static String root = ArsTechnica.MODID;

    public static class EnchantingAppProvider extends ApparatusRecipeProvider implements IConditionBuilder {

        public EnchantingAppProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void collectJsons(CachedOutput cache) {
            addIngredientRecipes();
            addEquipmentRecipes();
            addCurioRecipes();
            addThreadRecipes();

            Path output = this.generator.getPackOutput().getOutputFolder();
            for (ApparatusRecipeBuilder.RecipeWrapper<? extends EnchantingApparatusRecipe> g : recipes) {
                if (g != null) {
                    Path path = getRecipePath(output, g.id().getPath());
                    saveStable(cache, g.serialize(), path);
                }
            }

        }

        protected void addIngredientRecipes() {
            recipes.add(builder()
                    .withResult(ItemRegistry.CALIBRATED_PRECISION_MECHANISM)
                    .withReagent(PRECISION_MECHANISM)
                    .withPedestalItem(4, Ingredient.of(Items.AMETHYST_SHARD))
                    .withPedestalItem(4, Ingredient.of(SOURCE_GEM))
                    .withSourceCost(500)
                    .build());

            // Mark of Technomancy recipe
            recipes.add(builder()
                    .withResult(new ItemStack(ItemRegistry.MARK_OF_TECHNOMANCY.get(), 5))
                    .withReagent(WILDEN_TRIBUTE)
                    .withPedestalItem(PRECISION_MECHANISM)
                    .withPedestalItem(ItemRegistry.CALIBRATED_PRECISION_MECHANISM)
                    .withPedestalItem(MANIPULATION_ESSENCE)
                    .withPedestalItem(Ingredient.fromValues(java.util.stream.Stream.of(new Ingredient.TagValue(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ingots/iron"))))))
                    .withPedestalItem(Ingredient.fromValues(java.util.stream.Stream.of(new Ingredient.TagValue(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ingots/copper"))))))
                    .withPedestalItem(Ingredient.fromValues(java.util.stream.Stream.of(new Ingredient.TagValue(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "plates/brass"))))))
                    .withPedestalItem(Ingredient.fromValues(java.util.stream.Stream.of(new Ingredient.TagValue(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ingots/zinc"))))))
                    .withPedestalItem(Ingredient.fromValues(java.util.stream.Stream.of(new Ingredient.TagValue(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "plates/gold"))))))
                    .withSourceCost(10000)
                    .build());
        }

        protected void addEquipmentRecipes() {
            recipes.add(builder()
                    .withResult(ItemRegistry.RUNIC_SPANNER)
                    .withReagent(WRENCH)
                    .withPedestalItem(Ingredient.fromValues(java.util.stream.Stream.of(new Ingredient.TagValue(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ingots/gold"))))))
                    .withPedestalItem(Ingredient.of(new ItemStack(ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get())))
                    .withPedestalItem(Ingredient.of(MANIPULATION_ESSENCE))
                    .withSourceCost(500)
                    .build());

        }


        protected void addCurioRecipes() {
            recipes.add(builder()
                    .withResult(ItemRegistry.TRANSMUTATION_FOCUS)
                    .withReagent(MANIPULATION_ESSENCE)
                    .withPedestalItem(1, Ingredient.fromValues(java.util.stream.Stream.of(new Ingredient.TagValue(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ingots/brass"))))))
                    .withPedestalItem(Items.RABBIT_FOOT)
                    .withPedestalItem(ItemRegistry.CALIBRATED_PRECISION_MECHANISM)
                    .withPedestalItem(Ingredient.of(Items.EMERALD))
                    .build());

            recipes.add(builder()
                    .withResult(ItemRegistry.SPY_MONOCLE)
                    .withReagent(Items.SPYGLASS)
                    .withPedestalItem(Ingredient.of(new ItemStack(ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get())))
                    .build());
        }

        protected void addThreadRecipes() {
            recipes.add(builder()
                    .withResult(getPerkItem(PressurePerk.INSTANCE.getRegistryName()))
                    .withReagent(BLANK_THREAD)
                    .withPedestalItem(3, Ingredient.of(AIR_ESSENCE))
                    .withPedestalItem(Ingredient.of(COPPER_BACKTANK))
                    .withPedestalItem(Ingredient.of(new ItemStack(ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get())))
                    .build());
        }

        protected static Path getRecipePath(Path pathIn, String str) {
            return pathIn.resolve("data/" + root + "/recipe/" + str + ".json");
        }

        @Override
        public @NotNull String getName() {
            return "Example Apparatus";
        }
    }

    public static class ImbuementProvider extends ImbuementRecipeProvider {

        public ImbuementProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
            collectJsons(pOutput);
            List<CompletableFuture<?>> futures = new ArrayList<>();
            return provider.thenCompose((registry) -> {
                for (ImbuementRecipe g : recipes) {
                    Path path = getRecipePath(output, g.id.getPath());
                    futures.add(DataProvider.saveStable(pOutput, registry, ImbuementRecipe.CODEC, g, path));
                }
                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            });
        }

        @Override
        public void collectJsons(CachedOutput cache) {

            /*
            recipes.add(new ImbuementRecipe("example_focus", Ingredient.of(Items.AMETHYST_SHARD), new ItemStack(ItemsRegistry.SUMMONING_FOCUS, 1), 5000)
                    .withPedestalItem(ItemsRegistry.WILDEN_TRIBUTE)
            );
            */
        }

        protected Path getRecipePath(Path pathIn, String str) {
            return pathIn.resolve("data/" + root + "/recipe/" + str + ".json");
        }

        @Override
        public @NotNull String getName() {
            return "Example Imbuement";
        }

    }

    public static class GlyphProvider implements DataProvider {
        private final DataGenerator generator;

        public GlyphProvider(DataGenerator generatorIn) {
            this.generator = generatorIn;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
            Path output = this.generator.getPackOutput().getOutputFolder();
            
            List<CompletableFuture<?>> futures = new ArrayList<>();
            
            // Glyph recipes
            futures.add(saveGlyphRecipe(cache, output, "glyph_carve", 27, 
                List.of("ars_nouveau:manipulation_essence", "minecraft:crafting_table", "minecraft:cobblestone_stairs", "minecraft:cobblestone_slab", "minecraft:cobblestone_wall"),
                "ars_technica:glyph_carve"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_pack", 27,
                List.of("ars_nouveau:manipulation_essence", "minecraft:crafting_table", "minecraft:chest", "minecraft:iron_block"),
                "ars_technica:glyph_pack"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_polish", 55,
                List.of("ars_nouveau:manipulation_essence", "create:sand_paper"),
                "ars_technica:glyph_polish"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_obliterate", 160,
                List.of("ars_nouveau:manipulation_essence", "minecraft:anvil", "minecraft:diamond_block"),
                "ars_technica:glyph_obliterate"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_press", 55,
                List.of("ars_nouveau:manipulation_essence", "create:mechanical_press"),
                "ars_technica:glyph_press"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_superheat", 160,
                List.of("ars_nouveau:fire_essence", "ars_nouveau:fire_essence", "ars_nouveau:fire_essence", "minecraft:blaze_rod", "create:blaze_cake"),
                "ars_technica:glyph_superheat"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_fuse", 55,
                List.of("ars_nouveau:manipulation_essence", "ars_nouveau:fire_essence", "minecraft:blaze_rod", "minecraft:blaze_rod", "minecraft:blaze_rod"),
                "ars_technica:glyph_fuse"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_whirl", 55,
                List.of("ars_nouveau:manipulation_essence", "ars_nouveau:air_essence", "ars_nouveau:air_essence", "ars_nouveau:air_essence"),
                "ars_technica:glyph_whirl"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_insert", 27,
                List.of("minecraft:chest", "minecraft:chest"),
                "ars_technica:glyph_insert"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_telefeast", 55,
                List.of("ars_nouveau:manipulation_essence", "minecraft:golden_apple", "minecraft:bucket", "minecraft:glass_bottle", "minecraft:ender_pearl"),
                "ars_technica:glyph_telefeast"));
            
            futures.add(saveGlyphRecipe(cache, output, "glyph_apply", 27,
                List.of("ars_nouveau:manipulation_essence", "create:brass_hand"),
                "ars_technica:glyph_apply"));

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        }

        private CompletableFuture<?> saveGlyphRecipe(CachedOutput cache, Path output, String name, int exp, List<String> inputs, String result) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "ars_nouveau:glyph");
            json.addProperty("exp", exp);
            
            com.google.gson.JsonArray inputsArray = new com.google.gson.JsonArray();
            for (String input : inputs) {
                JsonObject inputObj = new JsonObject();
                inputObj.addProperty("item", input);
                inputsArray.add(inputObj);
            }
            json.add("inputs", inputsArray);
            
            JsonObject outputObj = new JsonObject();
            outputObj.addProperty("count", 1);
            outputObj.addProperty("id", result);
            json.add("output", outputObj);

            Path path = output.resolve("data/" + root + "/recipe/" + name + ".json");
            return DataProvider.saveStable(cache, json, path);
        }

        @Override
        public @NotNull String getName() {
            return "Glyph Recipes";
        }
    }

    public static class DyeProvider implements DataProvider {
        private final DataGenerator generator;

        public DyeProvider(DataGenerator generatorIn) {
            this.generator = generatorIn;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
            Path output = this.generator.getPackOutput().getOutputFolder();
            
            List<CompletableFuture<?>> futures = new ArrayList<>();
            
            // Dye recipes
            futures.add(saveDyeRecipe(cache, output, "dye_techno_hat", "ars_technica:technomancer_helmet"));
            futures.add(saveDyeRecipe(cache, output, "dye_techno_robes", "ars_technica:technomancer_chestplate"));
            futures.add(saveDyeRecipe(cache, output, "dye_techno_leggings", "ars_technica:technomancer_leggings"));
            futures.add(saveDyeRecipe(cache, output, "dye_techno_boots", "ars_technica:technomancer_boots"));

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        }

        private CompletableFuture<?> saveDyeRecipe(CachedOutput cache, Path output, String name, String armorItem) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "ars_nouveau:dye");
            json.addProperty("category", "misc");
            
            com.google.gson.JsonArray ingredients = new com.google.gson.JsonArray();
            
            JsonObject dyeIngredient = new JsonObject();
            dyeIngredient.addProperty("tag", "c:dyes");
            ingredients.add(dyeIngredient);
            
            JsonObject armorIngredient = new JsonObject();
            armorIngredient.addProperty("item", armorItem);
            ingredients.add(armorIngredient);
            
            json.add("ingredients", ingredients);
            
            JsonObject result = new JsonObject();
            result.addProperty("count", 1);
            result.addProperty("id", armorItem);
            json.add("result", result);

            Path path = output.resolve("data/" + root + "/recipe/" + name + ".json");
            return DataProvider.saveStable(cache, json, path);
        }

        @Override
        public @NotNull String getName() {
            return "Dye Recipes";
        }
    }

    public static class CraftingProvider extends RecipeProvider {
        
        public CraftingProvider(DataGenerator generatorIn) {
            super(generatorIn.getPackOutput(), provider);
        }

        @Override
        protected void buildRecipes(@NotNull RecipeOutput output) {
            // Source Motor - shaped recipe
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.SOURCE_MOTOR.get())
                .pattern("BEB")
                .pattern("CSC")
                .pattern("BCB")
                .define('B', Ingredient.fromValues(java.util.stream.Stream.of(new Ingredient.TagValue(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ingots/brass"))))))
                .define('C', Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "cogwheel"))))
                .define('S', ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get())
                .define('E', ELECTRON_TUBE.get())
                .unlockedBy("has_calibrated_precision_mechanism", has(ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get()))
                .save(output);

            // Experience gem recipes - shapeless
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.GIANT_EXPERIENCE_GEM.get())
                .requires(GREATER_EXPERIENCE_GEM.get(), 4)
                .unlockedBy("has_greater_experience_gem", has(GREATER_EXPERIENCE_GEM.get()))
                .save(output);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.GARGANTUAN_EXPERIENCE_GEM.get())
                .requires(ItemRegistry.GIANT_EXPERIENCE_GEM.get(), 4)
                .unlockedBy("has_giant_experience_gem", has(ItemRegistry.GIANT_EXPERIENCE_GEM.get()))
                .save(output);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, GREATER_EXPERIENCE_GEM.get(), 4)
                .requires(ItemRegistry.GIANT_EXPERIENCE_GEM.get())
                .unlockedBy("has_giant_experience_gem", has(ItemRegistry.GIANT_EXPERIENCE_GEM.get()))
                .save(output, ResourceLocation.fromNamespaceAndPath(root, "greater_from_giant_experience_gem"));

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.GIANT_EXPERIENCE_GEM.get(), 4)
                .requires(ItemRegistry.GARGANTUAN_EXPERIENCE_GEM.get())
                .unlockedBy("has_gargantuan_experience_gem", has(ItemRegistry.GARGANTUAN_EXPERIENCE_GEM.get()))
                .save(output, ResourceLocation.fromNamespaceAndPath(root, "giant_from_gargantuan_experience_gem"));
        }
    }

    public static class ArmorUpgradeProvider implements DataProvider {
        private final DataGenerator generator;

        public ArmorUpgradeProvider(DataGenerator generatorIn) {
            this.generator = generatorIn;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
            Path output = this.generator.getPackOutput().getOutputFolder();
            
            List<CompletableFuture<?>> futures = new ArrayList<>();
            
            System.out.println("ArmorUpgradeProvider: Starting recipe generation");
            
            // Conditional recipes (when ars_elemental is loaded)
            System.out.println("ArmorUpgradeProvider: Adding conditional recipes");
            futures.add(saveArmorUpgradeRecipe(cache, output, "technomancer_helmet", 
                "ars_elemental:mark_of_mastery", "ars_nouveau:magic_hood", "ars_technica:technomancer_helmet",
                List.of("ars_elemental:mark_of_mastery", "c:ingots/netherite", "c:ingots/brass", "create:goggles"), true));
            
            futures.add(saveArmorUpgradeRecipe(cache, output, "technomancer_chestplate",
                "ars_elemental:mark_of_mastery", "ars_nouveau:magic_robe", "ars_technica:technomancer_chestplate", 
                List.of("ars_elemental:mark_of_mastery", "c:ingots/netherite", "c:ingots/brass", "c:ingots/brass"), true));
            
            futures.add(saveArmorUpgradeRecipe(cache, output, "technomancer_leggings",
                "ars_elemental:mark_of_mastery", "ars_nouveau:magic_legs", "ars_technica:technomancer_leggings",
                List.of("ars_elemental:mark_of_mastery", "c:ingots/netherite", "c:ingots/brass", "c:ingots/brass"), true));
            
            futures.add(saveArmorUpgradeRecipe(cache, output, "technomancer_boots",
                "ars_elemental:mark_of_mastery", "ars_nouveau:magic_boots", "ars_technica:technomancer_boots",
                List.of("ars_elemental:mark_of_mastery", "c:ingots/netherite", "c:ingots/brass", "c:ingots/brass"), true));

            // Default recipes (no conditions)
            System.out.println("ArmorUpgradeProvider: Adding default recipes");
            futures.add(saveArmorUpgradeRecipe(cache, output, "technomancer_helmet_default",
                "ars_technica:mark_of_technomancy", "ars_nouveau:magic_hood", "ars_technica:technomancer_helmet",
                List.of("ars_technica:mark_of_technomancy", "c:ingots/netherite", "c:ingots/brass", "create:goggles"), false));
            
            futures.add(saveArmorUpgradeRecipe(cache, output, "technomancer_chestplate_default",
                "ars_technica:mark_of_technomancy", "ars_nouveau:magic_robe", "ars_technica:technomancer_chestplate",
                List.of("ars_technica:mark_of_technomancy", "c:ingots/netherite", "c:ingots/brass", "c:ingots/brass"), false));
            
            futures.add(saveArmorUpgradeRecipe(cache, output, "technomancer_leggings_default",
                "ars_technica:mark_of_technomancy", "ars_nouveau:magic_legs", "ars_technica:technomancer_leggings",
                List.of("ars_technica:mark_of_technomancy", "c:ingots/netherite", "c:ingots/brass", "c:ingots/brass"), false));
            
            futures.add(saveArmorUpgradeRecipe(cache, output, "technomancer_boots_default",
                "ars_technica:mark_of_technomancy", "ars_nouveau:magic_boots", "ars_technica:technomancer_boots",
                List.of("ars_technica:mark_of_technomancy", "c:ingots/netherite", "c:ingots/brass", "c:ingots/brass"), false));

            System.out.println("ArmorUpgradeProvider: Added " + futures.size() + " recipe futures");
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        }

        private CompletableFuture<?> saveArmorUpgradeRecipe(CachedOutput cache, Path output, String name, 
                String markItem, String reagentTag, String resultItem, List<String> pedestalItems, boolean conditional) {
            System.out.println("ArmorUpgradeProvider: Generating recipe " + name + " (conditional: " + conditional + ")");
            JsonObject json = new JsonObject();
            
            // Add conditions for conditional recipes
            if (conditional) {
                System.out.println("ArmorUpgradeProvider: Adding conditions for " + name);
                com.google.gson.JsonArray conditions = new com.google.gson.JsonArray();
                JsonObject condition = new JsonObject();
                condition.addProperty("type", "neoforge:mod_loaded");
                condition.addProperty("modid", "ars_elemental");
                conditions.add(condition);
                json.add("neoforge:conditions", conditions);
            }
            
            json.addProperty("type", "ars_technica:armor_upgrade");
            
            // Add pedestal items
            com.google.gson.JsonArray pedestalArray = new com.google.gson.JsonArray();
            for (String item : pedestalItems) {
                JsonObject pedestalItem = new JsonObject();
                if (item.contains(":") && !item.startsWith("c:")) {
                    pedestalItem.addProperty("item", item);
                } else {
                    pedestalItem.addProperty("tag", item);
                }
                pedestalArray.add(pedestalItem);
            }
            json.add("pedestalItems", pedestalArray);
            
            // Add reagent
            JsonObject reagent = new JsonObject();
            reagent.addProperty("tag", reagentTag);
            json.add("reagent", reagent);
            
            // Add result
            JsonObject result = new JsonObject();
            result.addProperty("count", 1);
            result.addProperty("id", resultItem);
            json.add("result", result);
            
            // Add source cost
            json.addProperty("sourceCost", 7000);
            
            // Add keepNbtOfReagent for all armor recipes
            json.addProperty("keepNbtOfReagent", true);

            Path path = output.resolve("data/" + root + "/recipe/" + name + ".json");
            System.out.println("ArmorUpgradeProvider: Saving recipe to " + path);
            return DataProvider.saveStable(cache, json, path);
        }

        @Override
        public @NotNull String getName() {
            return "Armor Upgrade Recipes";
        }
    }

}
