package net.mcreator.ars_technica.common.helpers;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.mcreator.ars_technica.common.entity.fusion.ArcaneFusionType;
import net.mcreator.ars_technica.common.entity.fusion.fluids.FluidSourceProvider;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.*;
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

    public static class FillingResult {
        public final ItemStack output;
        public final int fluidAmount;

        public FillingResult(ItemStack output, int fluidAmount) {
            this.output = output;
            this.fluidAmount = fluidAmount;
        }
    }

    public static Optional<FillingResult> getSpoutFillingRecipe(FluidStack fluidIngredient, ItemStack itemIngredient, Level world) {
        RecipeManager recipeManager = world.getRecipeManager();
        var allFillingRecipes = recipeManager.getAllRecipesFor(AllRecipeTypes.FILLING.getType());

        // Check for registered FillingRecipes
        var staticRecipe = allFillingRecipes
                .stream()
                .filter(FillingRecipe.class::isInstance)
                .map(FillingRecipe.class::cast)
                .filter(recipe -> recipe.getFluidIngredients().get(0).getMatchingFluidStacks().stream()
                        .anyMatch(stack -> stack.getFluid().isSame(fluidIngredient.getFluid())))
                .filter(recipe -> recipe.getIngredients().get(0).getItems()[0].getItem() == itemIngredient.getItem())
                .findFirst();
        if (staticRecipe.isPresent()) {
            var recipe = staticRecipe.get();
            var result = new FillingResult(recipe.getResultItem(world.registryAccess()).copy(), recipe.getRequiredFluid().getRequiredAmount());
            return Optional.of(result);
        }

        if(GenericItemFilling.canItemBeFilled(world, itemIngredient)) {
            int requiredAmount = GenericItemFilling.getRequiredAmountForItem(world, itemIngredient, fluidIngredient);
            if (requiredAmount <= fluidIngredient.getAmount()) {
                var itemResult = GenericItemFilling.fillItem(world, requiredAmount, itemIngredient, fluidIngredient);
                var result = new FillingResult(itemResult, 0); // 0 because fillItem already drained the fluid
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }


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