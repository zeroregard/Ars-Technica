package com.zeroregard.ars_technica.helpers;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.zeroregard.ars_technica.entity.fusion.ArcaneFusionType;
import com.zeroregard.ars_technica.entity.fusion.fluids.FluidSourceProvider;
import com.zeroregard.ars_technica.recipe.FuseMixingRecipe;
import com.zeroregard.ars_technica.registry.RecipeRegistry;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.*;

public class MixingRecipeHelpers {

    public static Optional<MixingRecipeResult> getMixingRecipe(List<ItemEntity> items, List<FluidSourceProvider> fluids, Level world, ArcaneFusionType fusionType) {
        RecipeManager recipeManager = world.getRecipeManager();
        var fuseRecipes = gatherEligibleRecipes(recipeManager, fusionType);

        for (FuseRecipeLike fuseRecipe : fuseRecipes) {
            ArrayList<ItemEntity> usedEntities = new ArrayList<>();
            ArrayList<FluidSourceProvider> usedFluids = new ArrayList<>();

            boolean matches = mixingRecipeIngredientsMatch(fuseRecipe, items, fluids, usedEntities, usedFluids);

            if (matches) {
                MixingRecipeResult result = new MixingRecipeResult(fuseRecipe, usedEntities, usedFluids);
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }

    private static List<FuseRecipeLike> gatherEligibleRecipes(RecipeManager recipeManager, ArcaneFusionType fusionType) {
        ArrayList<FuseRecipeLike> fuseRecipes = new ArrayList<>();
        for (RecipeHolder<?> holder : recipeManager.getAllRecipesFor(AllRecipeTypes.MIXING.getType())) {
            if (FuseRecipeFilter.isFiltered(holder.id())) {
                continue;
            }
            var value = holder.value();
            if (value instanceof MixingRecipe recipe && recipe.getRequiredHeat() == fusionType.getSuppliedHeat()) {
                fuseRecipes.add(new CreateMixingRecipeAdapter(recipe));
            }
        }

        for (RecipeHolder<?> holder : recipeManager.getAllRecipesFor(RecipeRegistry.FUSE_MIXING_TYPE.get())) {
            if (FuseRecipeFilter.isFiltered(holder.id())) {
                continue;
            }
            var value = holder.value();
            if (value instanceof FuseMixingRecipe recipe && recipe.getRequiredHeat() == fusionType.getSuppliedHeat()) {
                fuseRecipes.add(recipe);
            }
        }
        return fuseRecipes;
    }

    public static class MixingRecipeResult {
        public FuseRecipeLike recipe;
        public List<ItemEntity> usedEntities;
        public List<FluidSourceProvider> usedFluids;

        public MixingRecipeResult(FuseRecipeLike recipe, List<ItemEntity> usedEntities, List<FluidSourceProvider> usedFluids) {
            this.recipe = recipe;
            this.usedEntities = usedEntities;
            this.usedFluids = usedFluids;
        }
    }

    private static boolean mixingRecipeIngredientsMatch(
            FuseRecipeLike recipe,
            List<ItemEntity> availableItems,
            List<FluidSourceProvider> availableFluids,
            List<ItemEntity> usedEntities,
            List<FluidSourceProvider> usedFluids
    ) {

        Map<ItemEntity, Integer> usageMap = new HashMap<>();

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

        for (SizedFluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            var fluidCandidate = availableFluids.stream()
                    .filter(fluid -> fluidIngredient.ingredient().test(fluid.getFluidStack()))
                    .findFirst();
            if(fluidCandidate.isPresent()) {
                var candidateUnwrapped = fluidCandidate.get();
                if(candidateUnwrapped.getMbAmount() >= fluidIngredient.amount()) {
                    usedFluids.add(candidateUnwrapped);
                }
            }
        }

        return usedEntities.size() == recipe.getIngredients().size() && usedFluids.size() == recipe.getFluidIngredients().size();
    }

    private record CreateMixingRecipeAdapter(MixingRecipe delegate) implements FuseRecipeLike {
        @Override
        public com.simibubi.create.content.processing.recipe.HeatCondition getRequiredHeat() {
            return delegate.getRequiredHeat();
        }

        @Override
        public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
            return delegate.getIngredients();
        }

        @Override
        public net.minecraft.core.NonNullList<SizedFluidIngredient> getFluidIngredients() {
            return delegate.getFluidIngredients();
        }

        @Override
        public net.minecraft.core.NonNullList<FluidStack> getFluidResults() {
            return delegate.getFluidResults();
        }

        @Override
        public net.minecraft.world.item.ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider access) {
            return delegate.getResultItem(access);
        }
    }
}
