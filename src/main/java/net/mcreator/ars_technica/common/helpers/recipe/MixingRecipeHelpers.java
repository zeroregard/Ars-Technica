package net.mcreator.ars_technica.common.helpers.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import net.mcreator.ars_technica.common.entity.fusion.ArcaneFusionType;
import net.mcreator.ars_technica.common.entity.fusion.fluids.FluidSourceProvider;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

public class MixingRecipeHelpers {

    public static Optional<MixingRecipeResult> getMixingRecipe(List<ItemEntity> items, List<FluidSourceProvider> fluids, Level world, ArcaneFusionType fusionType) {
        RecipeManager recipeManager = world.getRecipeManager();
        List<MixingRecipe> mixingRecipes = recipeManager
                .getAllRecipesFor(AllRecipeTypes.MIXING.getType())
                .stream()
                .filter(MixingRecipe.class::isInstance)
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
        Map<Fluid, List<FluidSourceProvider>> fluidMap = availableFluids.stream()
                .collect(Collectors.groupingBy(provider -> provider.getFluidStack().getFluid()));


        List<FluidStack> requiredFluidStacks = recipe.getFluidIngredients().stream()
                .flatMap(ingredient -> ingredient.getMatchingFluidStacks().stream())
                .collect(Collectors.toList());

        // Match item requirements
        for (Ingredient itemIngredient : recipe.getIngredients()) {
            for(ItemStack ingredientVariant : itemIngredient.getItems()) {
                var itemCandidate = availableItems.stream().filter(item -> item.getItem().getItem() == ingredientVariant.getItem()).findFirst();
                itemCandidate.ifPresent(usedEntities::add);
            }
        }

        if (usedEntities.size() < recipe.getIngredients().size()) {
            return false;
        }

        // Match fluid requirements
        for (FluidStack requiredFluid : requiredFluidStacks) {
            List<FluidSourceProvider> candidates = fluidMap.getOrDefault(requiredFluid.getFluid(), new ArrayList<>());
            boolean matched = false;

            // Try to find a matching fluid
            for (Iterator<FluidSourceProvider> it = candidates.iterator(); it.hasNext(); ) {
                FluidSourceProvider candidate = it.next();
                if (candidate.getMbAmount() >= requiredFluid.getAmount()) {
                    usedFluids.add(candidate);
                    it.remove(); // Remove from available pool
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return false; // Failed to match a fluid
            }
        }

        return usedEntities.size() == recipe.getIngredients().size();
    }
}
