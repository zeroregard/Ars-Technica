package com.zeroregard.ars_technica.helpers;


import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeHelpers {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<ItemStack> rollResultsWithFortuneBoost(ProcessingRecipe recipe, RandomSource random, float chanceMultiplier, boolean allowOverflow) {
        List<ItemStack> results = new ArrayList<>();
        List<ProcessingOutput> rollables = recipe.getRollableResults();

        for (ProcessingOutput rollable : rollables) {
            float adjustedChance = rollable.getChance() * chanceMultiplier;
            if (!allowOverflow) {
                adjustedChance = Math.min(1.0f, adjustedChance);
            }
            adjustedChance = Math.max(0.0f, adjustedChance);

            int guaranteedRolls = (int) adjustedChance;
            float partialChance = adjustedChance - guaranteedRolls;

            for (int guaranteed = 0; guaranteed < guaranteedRolls; guaranteed++) {
                ItemStack guaranteedStack = rollable.getStack().copy();
                if (!guaranteedStack.isEmpty()) {
                    results.add(guaranteedStack);
                }
            }

            if (random.nextFloat() < partialChance) {
                ItemStack stack = rollable.getStack().copy();
                if (!stack.isEmpty()) {
                    results.add(stack);
                }
            }
        }

        return results;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean isChanceBased(ItemStack input, ProcessingRecipe recipe) {
        List<ProcessingOutput> rollables = recipe.getRollableResults();

        return rollables.stream()
                .anyMatch(rollable -> input.getItem() == rollable.getStack().getItem() && rollable.getChance() < 1);
    }


    public static Optional<RecipeHolder<PressingRecipe>> getPressingRecipeForItemStack(ItemStack input, Level world) {
        SingleRecipeInput wrapper = new SingleRecipeInput(input);
        return world.getRecipeManager().getRecipeFor(AllRecipeTypes.PRESSING.getType(), wrapper, world);
    }

    public static Optional<AbstractCrushingRecipe> getCrushingRecipeForItemStack(ItemStack input, Level world) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(input);

        Optional<RecipeHolder<AbstractCrushingRecipe>> recipeHolder =
                world.getRecipeManager().getRecipeFor(AllRecipeTypes.CRUSHING.getType(), recipeInput, world);

        if (recipeHolder.isEmpty()) {
            recipeHolder = world.getRecipeManager().getRecipeFor(AllRecipeTypes.MILLING.getType(), recipeInput, world);
        }

        return recipeHolder.map(RecipeHolder::value);
    }

    public static Optional<SplashingRecipe> getSplashingRecipeForItemStack(ItemStack input, Level world) {
        SingleRecipeInput wrapper = new SingleRecipeInput(input);

        Optional<RecipeHolder<SplashingRecipe>> recipeHolder =
                world.getRecipeManager().getRecipeFor(AllRecipeTypes.SPLASHING.getType(), wrapper, world);

        return recipeHolder.map(RecipeHolder::value);
    }

    public static Optional<HauntingRecipe> getHauntingRecipeForItemStack(ItemStack input, Level world) {
        SingleRecipeInput wrapper = new SingleRecipeInput(input);

        Optional<RecipeHolder<HauntingRecipe>> recipeHolder =
                world.getRecipeManager().getRecipeFor(AllRecipeTypes.HAUNTING.getType(), wrapper, world);

        return recipeHolder.map(RecipeHolder::value);
    }

    public static Optional<RecipeHolder<Recipe<RecipeInput>>> getItemApplicationRecipeForItemStack(ItemStack input, Level world) {
        ItemStackHandler itemHandler = new ItemStackHandler(1);
        itemHandler.setStackInSlot(0, input);
        RecipeWrapper wrapper = new RecipeWrapper(itemHandler);
        
        return world.getRecipeManager().getRecipeFor(AllRecipeTypes.ITEM_APPLICATION.getType(), wrapper, world);
    }

    public static Optional<RecipeHolder<ManualApplicationRecipe>> getItemApplicationRecipe(ItemStack applyItem, ItemStack target, Level world) {
        RecipeType<ManualApplicationRecipe> type = AllRecipeTypes.ITEM_APPLICATION.getType();

        var allApplicationRecipes = world.getRecipeManager().getAllRecipesFor(type);
        
        for (var recipeHolder : allApplicationRecipes) {
            var recipe = recipeHolder.value();
            
            ItemStackHandler itemHandler = new ItemStackHandler(2);
            itemHandler.setStackInSlot(0, target);
            itemHandler.setStackInSlot(1, applyItem);
            RecipeWrapper wrapper = new RecipeWrapper(itemHandler);
            
            if (recipe.matches(wrapper, world)) {
                return Optional.of(recipeHolder);
            }
        }
        
        return Optional.empty();
    }

    public static Optional<RecipeHolder<DeployerApplicationRecipe>> getDeployingRecipe(ItemStack applyItem, ItemStack target, Level world) {
        RecipeType<DeployerApplicationRecipe> type = AllRecipeTypes.DEPLOYING.getType();

        var allDeployingRecipes = world.getRecipeManager().getAllRecipesFor(type);
        
        for (var recipeHolder : allDeployingRecipes) {
            var recipe = recipeHolder.value();
            
            ItemStackHandler itemHandler = new ItemStackHandler(2);
            itemHandler.setStackInSlot(0, target);
            itemHandler.setStackInSlot(1, applyItem);
            RecipeWrapper wrapper = new RecipeWrapper(itemHandler);
            
            if (recipe.matches(wrapper, world)) {
                return Optional.of(recipeHolder);
            }
        }
        
        return Optional.empty();
    }

    public static <R extends ProcessingRecipe<RecipeWrapper, ?>> Optional<RecipeHolder<R>> getSequencedAssemblyRecipe(RecipeType<R> type, Class<R> clazz, ItemStack applyItem, ItemStack target, Level world) {
        ItemStackHandler itemHandler = new ItemStackHandler(2);
        itemHandler.setStackInSlot(0, target);
        itemHandler.setStackInSlot(1, applyItem);
        RecipeWrapper wrapper = new RecipeWrapper(itemHandler);

        return SequencedAssemblyRecipe.getRecipe(world, wrapper, type, clazz);
    }

}