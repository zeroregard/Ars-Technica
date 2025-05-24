package com.zeroregard.ars_technica.helpers;


import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Optional;

public class RecipeHelpers {

    public static boolean isChanceBased(ItemStack input, ProcessingRecipe<?> recipe) {
        List<ProcessingOutput> rollables = recipe.getRollableResults();

        return rollables.stream()
                .anyMatch(rollable -> input.getItem() == rollable.getStack().getItem() && rollable.getChance() < 1);
    }


    public static Optional<RecipeHolder<Recipe<SingleRecipeInput>>> getPressingRecipeForItemStack(ItemStack input, Level world) {
        SingleRecipeInput wrapper = new SingleRecipeInput(input);
        return world.getRecipeManager().getRecipeFor(AllRecipeTypes.PRESSING.getType(), wrapper, world);
    }

    public static Optional<ProcessingRecipe<RecipeWrapper>> getCrushingRecipeForItemStack(ItemStack input, Level world) {
        ItemStackHandler itemHandler = new ItemStackHandler(1);
        itemHandler.setStackInSlot(0, input);
        RecipeWrapper wrapper = new RecipeWrapper(itemHandler);

        Optional<RecipeHolder<Recipe<RecipeWrapper>>> recipeHolder =
                world.getRecipeManager().getRecipeFor(AllRecipeTypes.CRUSHING.getType(), wrapper, world);

        if (recipeHolder.isEmpty()) {
            recipeHolder = world.getRecipeManager().getRecipeFor(AllRecipeTypes.MILLING.getType(), wrapper, world);
        }

        return recipeHolder
                .map(RecipeHolder::value)
                .filter(recipe -> recipe instanceof ProcessingRecipe)
                .map(recipe -> (ProcessingRecipe<RecipeWrapper>) recipe);
    }

    public static Optional<ProcessingRecipe<RecipeWrapper>> getSplashingRecipeForItemStack(ItemStack input, Level world) {
        return getProcessingRecipe(input, world, AllRecipeTypes.SPLASHING.getType());
    }

    public static Optional<ProcessingRecipe<RecipeWrapper>> getHauntingRecipeForItemStack(ItemStack input, Level world) {
        return getProcessingRecipe(input, world, AllRecipeTypes.HAUNTING.getType());
    }

    public static Optional<RecipeHolder<Recipe<RecipeInput>>> getItemApplicationRecipeForItemStack(ItemStack input, Level world) {
        ItemStackHandler itemHandler = new ItemStackHandler(1);
        itemHandler.setStackInSlot(0, input);
        RecipeWrapper wrapper = new RecipeWrapper(itemHandler);
        
        return world.getRecipeManager().getRecipeFor(AllRecipeTypes.ITEM_APPLICATION.getType(), wrapper, world);
    }

    private static <T extends ProcessingRecipe<RecipeWrapper>> Optional<T> getProcessingRecipe(
            ItemStack input, Level world, RecipeType<T> recipeType) {

        ItemStackHandler itemHandler = new ItemStackHandler(1);
        itemHandler.setStackInSlot(0, input);
        RecipeWrapper wrapper = new RecipeWrapper(itemHandler);

        Optional<RecipeHolder<T>> recipeHolder = world.getRecipeManager().getRecipeFor(recipeType, wrapper, world);

        return recipeHolder.map(RecipeHolder::value);
    }

    public static Optional<RecipeHolder<Recipe<RecipeInput>>> getItemApplicationRecipe(ItemStack applyItem, ItemStack target, Level world) {
        var allApplicationRecipes = world.getRecipeManager().getAllRecipesFor(AllRecipeTypes.ITEM_APPLICATION.getType());
        
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

}