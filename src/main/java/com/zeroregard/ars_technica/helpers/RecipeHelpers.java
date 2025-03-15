package com.zeroregard.ars_technica.helpers;


import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
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
}