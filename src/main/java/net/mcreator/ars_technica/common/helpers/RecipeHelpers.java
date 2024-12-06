package net.mcreator.ars_technica.common.helpers;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipeHelpers {

    public static Optional<SplashingRecipe> getSplashingRecipeForItemStack(
            RecipeManager recipeManager, ItemStack input, Level world) {

        SplashingRecipe.SplashingWrapper wrapper = new SplashingRecipe.SplashingWrapper();
        wrapper.setItem(0, input);

        return recipeManager.getRecipeFor(AllRecipeTypes.SPLASHING.getType(), wrapper, world);
    }

    public static Optional<HauntingRecipe> getHauntingRecipeForItemStack(
            RecipeManager recipeManager, ItemStack input, Level world) {

        HauntingRecipe.HauntingWrapper wrapper = new HauntingRecipe.HauntingWrapper();
        wrapper.setItem(0, input);

        return recipeManager.getRecipeFor(AllRecipeTypes.HAUNTING.getType(), wrapper, world);
    }

    public static Optional<PressingRecipe> getPressingRecipeForItemStack(ItemStack input, Level world) {
        return world.getRecipeManager()
                .getRecipeFor(AllRecipeTypes.PRESSING.getType(), new RecipeWrapper(new net.minecraftforge.items.ItemStackHandler(1) {
                    {
                        setStackInSlot(0, input);
                    }
                }), world);
    }

    public static Optional<ProcessingRecipe<RecipeWrapper>> getCrushingRecipeForItemStack(ItemStack input, Level world) {
        var wrapper = new RecipeWrapper(new net.minecraftforge.items.ItemStackHandler(1) {
            {
                setStackInSlot(0, input);
            }
        });
        Optional<ProcessingRecipe<RecipeWrapper>> crushingRecipe = world.getRecipeManager()
                .getRecipeFor(AllRecipeTypes.CRUSHING.getType(), wrapper, world);
        if (!crushingRecipe.isPresent()) {
            return world.getRecipeManager()
                    .getRecipeFor(AllRecipeTypes.MILLING.getType(), wrapper, world);
        }
        return crushingRecipe;
    }


    public static Optional<SandPaperPolishingRecipe> getPolishingRecipeForItemStack(ItemStack input, Level world) {
        SandPaperPolishingRecipe.SandPaperInv sandpaperInventory = new SandPaperPolishingRecipe.SandPaperInv(input);
        return world.getRecipeManager()
                .getRecipeFor(AllRecipeTypes.SANDPAPER_POLISHING.getType(), sandpaperInventory, world);
    }

    public static boolean isChanceBased(ItemStack input, ProcessingRecipe<?> recipe) {
        List<ProcessingOutput> rollables = recipe.getRollableResults();

        return rollables.stream()
                .anyMatch(rollable -> input.getItem() == rollable.getStack().getItem() && rollable.getChance() < 1);
    }

    private static <C extends Container, T extends Recipe<C>> Optional<T> getRecipeFor(
            RecipeManager recipeManager, RecipeType<T> recipeType, C container, Level world) {
        return recipeManager.getRecipeFor(recipeType, container, world);
    }


    // Fusion is slightly different from regular mixing:
    // There has to be at least 2 items and liquids are not considered (only as output for now)
    public static Optional<Pair<MixingRecipe, List<ItemEntity>>> getFusionRecipeForItems(List<ItemEntity> items, Level world) {
        RecipeManager recipeManager = world.getRecipeManager();
        List<MixingRecipe> mixingRecipes = recipeManager.getAllRecipesFor(AllRecipeTypes.MIXING.getType());

        for (MixingRecipe mixingRecipe : mixingRecipes) {
            // Check if recipe matches
            ArrayList<ItemEntity> usedEntities = new ArrayList<>();
            boolean matches = fusionRecipeIngredientsMatch(mixingRecipe, items, usedEntities);

            if (matches) {
                return Optional.of(Pair.of(mixingRecipe, usedEntities));
            }
        }

        return Optional.empty();
    }

    private static boolean fusionRecipeIngredientsMatch(MixingRecipe recipe, List<ItemEntity> availableItems, List<ItemEntity> usedEntities) {
        List<ItemStack> requiredStacks = recipe.getIngredients().stream()
                .flatMap(ingredient -> Arrays.stream(ingredient.getItems()))
                .collect(Collectors.toList());

        // Attempt to satisfy all required ingredients
        List<ItemEntity> remainingItems = new ArrayList<>(availableItems);

        for (ItemStack requiredStack : requiredStacks) {
            boolean satisfied = false;

            for (ItemEntity entity : remainingItems) {
                ItemStack entityStack = entity.getItem();

                if (entityStack.getItem() == requiredStack.getItem() && entityStack.getCount() >= requiredStack.getCount()) {
                    usedEntities.add(entity);
                    remainingItems.remove(entity);
                    satisfied = true;
                    break;
                }
            }

            if (!satisfied) {
                return false;
            }
        }

        if (usedEntities.size() < 2) {
            return false;
        }

        return true;
    }
}
