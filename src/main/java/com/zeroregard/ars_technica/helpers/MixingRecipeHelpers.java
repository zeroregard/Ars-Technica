package com.zeroregard.ars_technica.helpers;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.zeroregard.ars_technica.entity.fusion.ArcaneFusionType;
import com.zeroregard.ars_technica.entity.fusion.fluids.FluidSourceProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;

public class MixingRecipeHelpers {

    public static Optional<MixingRecipeResult> getMixingRecipe(List<ItemEntity> items, List<FluidSourceProvider> fluids, Level world, ArcaneFusionType fusionType) {
        RecipeManager recipeManager = world.getRecipeManager();
        var mixingRecipes = recipeManager.getAllRecipesFor(AllRecipeTypes.MIXING.getType())
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe instanceof MixingRecipe)
                .map(MixingRecipe.class::cast)
                .filter(x -> x.getRequiredHeat() == fusionType.getSuppliedHeat())
                .toList();

        for (MixingRecipe mixingRecipe : mixingRecipes) {
            ArrayList<ItemEntity> usedEntities = new ArrayList<>();
            ArrayList<FluidSourceProvider> usedFluids = new ArrayList<>();


            boolean matches = mixingRecipeIngredientsMatch(mixingRecipe, items, fluids, usedEntities, usedFluids);

            if (matches) {
                MixingRecipeResult result = new MixingRecipeResult(mixingRecipe, usedEntities, usedFluids);
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }

    public static class MixingRecipeResult {
        public MixingRecipe recipe;
        public List<ItemEntity> usedEntities;
        public List<FluidSourceProvider> usedFluids;

        public MixingRecipeResult(MixingRecipe recipe, List<ItemEntity> usedEntities, List<FluidSourceProvider> usedFluids) {
            this.recipe = recipe;
            this.usedEntities = usedEntities;
            this.usedFluids = usedFluids;
        }
    }

    private static boolean mixingRecipeIngredientsMatch(
            MixingRecipe recipe,
            List<ItemEntity> availableItems,
            List<FluidSourceProvider> availableFluids,
            List<ItemEntity> usedEntities,
            List<FluidSourceProvider> usedFluids
    ) {

        // Dictionary to track how many times each item entity has been used, because some recipes call for the same or similar ingredient many times
        Map<ItemEntity, Integer> usageMap = new HashMap<>();

        // Match item requirements
        for (Ingredient itemIngredient : recipe.getIngredients()) {
            for(ItemStack ingredientVariant : itemIngredient.getItems()) {
                var itemCandidate = availableItems.stream().filter(item -> item.getItem().getItem() == ingredientVariant.getItem()).findFirst();
                if(itemCandidate.isPresent()) {
                    var candidateUnwrapped = itemCandidate.get();
                    int usedCount = usageMap.getOrDefault(candidateUnwrapped, 0);
                    var candidateItemCount = candidateUnwrapped.getItem().getCount();
                    if(candidateItemCount >= ingredientVariant.getCount() && candidateItemCount > usedCount) {
                        usedEntities.add(candidateUnwrapped);
                        usageMap.put(candidateUnwrapped, usedCount + 1);
                        break;
                    }
                }
            }
        }

        if (usedEntities.size() < recipe.getIngredients().size()) {
            return false;
        }

        // Match fluid requirements
        for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            for(FluidStack ingredientVariant : fluidIngredient.getMatchingFluidStacks()) {
                var fluidCandidate = availableFluids.stream().filter(fluid -> fluid.getFluidStack().getFluid() == ingredientVariant.getFluid()).findFirst();
                if(fluidCandidate.isPresent()) {
                    var candidateUnwrapped = fluidCandidate.get();
                    if(candidateUnwrapped.getMbAmount() >= ingredientVariant.getAmount()) {
                        usedFluids.add(candidateUnwrapped);
                        break;
                    }
                }
            }
        }

        return usedEntities.size() == recipe.getIngredients().size() && usedFluids.size() == recipe.getFluidIngredients().size();
    }
}
