package com.zeroregard.ars_technica.datagen;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.common.datagen.ImbuementRecipeProvider;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.zeroregard.ars_technica.ArsElementalModItems;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.recipe.TechnomancerArmorRecipe;
import com.zeroregard.ars_technica.registry.GlyphRegistry;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.MANIPULATION_ESSENCE;
import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.SOURCE_GEM;
import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;
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
            addTechnomancerArmorRecipes();
            addEquipmentRecipes();
            addCurioRecipes();

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
        }

        protected void addEquipmentRecipes() {
            recipes.add(builder()
                    .withResult(ItemRegistry.RUNIC_SPANNER)
                    .withReagent(WRENCH)
                    .withPedestalItem(Ingredient.of(Items.GOLD_INGOT))
                    .withPedestalItem(Ingredient.of(new ItemStack(ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get())))
                    .withPedestalItem(Ingredient.of(MANIPULATION_ESSENCE))
                    .withSourceCost(500)
                    .build());

        }


        protected void addCurioRecipes() {
            recipes.add(builder()
                    .withResult(ItemRegistry.TRANSMUTATION_FOCUS)
                    .withReagent(MANIPULATION_ESSENCE)
                    .withPedestalItem(1, BRASS_INGOT)
                    .withPedestalItem(Items.RABBIT_FOOT)
                    .withPedestalItem(ItemRegistry.CALIBRATED_PRECISION_MECHANISM)
                    .withPedestalItem(Ingredient.of(Items.EMERALD))
                    .build());
        }


        ArmorBuilder Abuilder() {
            return new ArmorBuilder();
        }

        public static class ArmorBuilder extends ApparatusRecipeBuilder {

            @Override
            public RecipeWrapper<EnchantingApparatusRecipe> build() {
                var wrapper = super.build();
                return new RecipeWrapper<>(wrapper.id(), new TechnomancerArmorRecipe(wrapper.recipe().reagent(), wrapper.recipe().result(), wrapper.recipe().pedestalItems(), wrapper.recipe().sourceCost()), TechnomancerArmorRecipe.CODEC);
            }
        }

        protected void addTechnomancerArmorRecipes() {
            recipes.add(Abuilder()
                            .withResult(ItemRegistry.TECHNOMANCER_HELMET.get())
                            .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_HOOD))
                            .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get())
                            .withPedestalItem(Items.NETHERITE_INGOT)
                            .withPedestalItem(BRASS_INGOT)
                            .withPedestalItem(GOGGLES)
                            .withSourceCost(7000)
                            .keepNbtOfReagent(true)
                            .build());
            recipes.add(
                    Abuilder()
                            .withResult(ItemRegistry.TECHNOMANCER_CHESTPLATE.get())
                            .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_ROBE))
                            .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get())
                            .withPedestalItem(Items.NETHERITE_INGOT)
                            .withPedestalItem(2, BRASS_INGOT)
                            .withSourceCost(7000)
                            .keepNbtOfReagent(true)
                            .build());
            recipes.add(
                    Abuilder()
                            .withResult((ItemRegistry.TECHNOMANCER_LEGGINGS.get()))
                            .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_LEG))
                            .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get())
                            .withPedestalItem(Items.NETHERITE_INGOT)
                            .withPedestalItem(2, BRASS_INGOT)
                            .withSourceCost(7000)
                            .keepNbtOfReagent(true)
                            .build());
            recipes.add(
                    Abuilder()
                            .withResult(ItemRegistry.TECHNOMANCER_BOOTS.get())
                            .withReagent(Ingredient.of(ATTagsProvider.ATItemTagsProvider.MAGIC_BOOT))
                            .withPedestalItem(ArsElementalModItems.MARK_OF_MASTERY.get())
                            .withPedestalItem(Items.NETHERITE_INGOT)
                            .withPedestalItem(2, BRASS_INGOT)
                            .withSourceCost(7000)
                            .keepNbtOfReagent(true)
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

    public static class PatchouliProvider extends com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider {

        public PatchouliProvider(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        public void collectJsons(CachedOutput cache) {

            for (AbstractSpellPart spell : GlyphRegistry.registeredSpells) {
                addGlyphPage(spell);
            }

            for (PatchouliPage patchouliPage : pages) {
                DataProvider.saveStable(cache, patchouliPage.build(), patchouliPage.path());
            }

        }

        @Override
        public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
            PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                    .withIcon(item.asItem())
                    .withPage(new TextPage(root + ".page." + getRegistryName(item.asItem()).getPath()))
                    .withPage(recipePage);
            var page = new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()).getPath()));
            this.pages.add(page);
            return page;
        }

        public void addFamiliarPage(AbstractFamiliarHolder familiarHolder) {
            PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity." + root + "." + familiarHolder.getRegistryName().getPath())
                    .withIcon(root + ":" + familiarHolder.getRegistryName().getPath())
                    .withTextPage(root + ".familiar_desc." + familiarHolder.getRegistryName().getPath())
                    .withPage(new EntityPage(familiarHolder.getRegistryName().toString()));
            this.pages.add(new PatchouliPage(builder, getPath(FAMILIARS, familiarHolder.getRegistryName().getPath())));
        }

        public void addRitualPage(AbstractRitual ritual) {
            PatchouliBuilder builder = new PatchouliBuilder(RITUALS, "item." + root + '.' + ritual.getRegistryName().getPath())
                    .withIcon(ritual.getRegistryName().toString())
                    .withTextPage(ritual.getDescriptionKey())
                    .withPage(new CraftingPage(root + ":tablet_" + ritual.getRegistryName().getPath()));

            this.pages.add(new PatchouliPage(builder, getPath(RITUALS, ritual.getRegistryName().getPath())));
        }

        public void addGlyphPage(AbstractSpellPart spellPart) {
            ResourceLocation category = switch (spellPart.defaultTier().value) {
                case 1 -> GLYPHS_1;
                case 2 -> GLYPHS_2;
                default -> GLYPHS_3;
            };
            PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
                    .withName(root + ".glyph_name." + spellPart.getRegistryName().getPath())
                    .withIcon(spellPart.getRegistryName().toString())
                    .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
                    .withPage(new TextPage(root + ".glyph_desc." + spellPart.getRegistryName().getPath()))
                    .withPage(new GlyphScribePage(spellPart));
            this.pages.add(new PatchouliPage(builder, getPath(category, spellPart.getRegistryName().getPath())));
        }

        /**
         * Gets a name for this provider, to use in logging.
         */
        @Override
        public @NotNull String getName() {
            return "Example Patchouli Datagen";
        }

        @Override
        public Path getPath(ResourceLocation category, String fileName) {
            return this.generator.getPackOutput().getOutputFolder().resolve("data/" + root + "/patchouli_books/example/en_us/entries/" + category.getPath() + "/" + fileName + ".json");
        }

        ImbuementPage ImbuementPage(ItemLike item) {
            return new ImbuementPage(root + ":imbuement_" + getRegistryName(item.asItem()).getPath());
        }

    }

}
