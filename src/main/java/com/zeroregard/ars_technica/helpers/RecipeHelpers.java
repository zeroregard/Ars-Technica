package com.zeroregard.ars_technica.helpers;


import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Result of matching a set of item entities to a Compacting recipe.
     * Consumption list indicates which entity to shrink and by how much.
     */
    public record CompactingMatch(RecipeHolder<CompactingRecipe> recipe, List<ConsumptionEntry> consumption) {
        public record ConsumptionEntry(ItemEntity entity, int count) {}
    }

    /**
     * Finds the first Compacting recipe that can be satisfied by the given item entities (item-only recipes).
     * Entities are considered in order of distance to posVec so closer items are preferred.
     */
    public static Optional<CompactingMatch> findCompactingMatch(List<ItemEntity> entities, Vec3 posVec, Level world) {
        if (entities == null || entities.isEmpty()) {
            return Optional.empty();
        }
        List<ItemEntity> sorted = new ArrayList<>(entities);
        sorted.removeIf(e -> e == null || e.isRemoved() || e.getItem().isEmpty());
        sorted.sort(Comparator.comparingDouble(e -> e.position().distanceToSqr(posVec)));

        Map<ItemEntity, Integer> pool = new HashMap<>();
        for (ItemEntity e : sorted) {
            int count = e.getItem().getCount();
            if (count > 0) {
                pool.put(e, count);
            }
        }
        if (pool.isEmpty()) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        List<RecipeHolder<CompactingRecipe>> recipes = (List<RecipeHolder<CompactingRecipe>>) (List<?>) world.getRecipeManager().getAllRecipesFor(AllRecipeTypes.COMPACTING.getType());

        for (RecipeHolder<CompactingRecipe> holder : recipes) {
            CompactingRecipe recipe = holder.value();
            if (!recipe.getFluidIngredients().isEmpty()) {
                continue;
            }
            List<Ingredient> ingredients = recipe.getIngredients();
            if (ingredients.isEmpty()) {
                continue;
            }
            Map<ItemEntity, Integer> poolCopy = new HashMap<>(pool);
            List<CompactingMatch.ConsumptionEntry> plan = new ArrayList<>();
            boolean matched = true;
            for (Ingredient ing : ingredients) {
                ItemEntity found = null;
                for (ItemEntity e : sorted) {
                    if (poolCopy.getOrDefault(e, 0) <= 0) continue;
                    if (!ing.test(e.getItem())) continue;
                    found = e;
                    break;
                }
                if (found == null) {
                    matched = false;
                    break;
                }
                poolCopy.merge(found, -1, Integer::sum);
                plan.add(new CompactingMatch.ConsumptionEntry(found, 1));
            }
            if (!matched) {
                continue;
            }
            // Merge plan: (entity, 1) + (entity, 1) -> (entity, 2)
            Map<ItemEntity, Integer> merged = new HashMap<>();
            for (CompactingMatch.ConsumptionEntry entry : plan) {
                merged.merge(entry.entity(), entry.count(), Integer::sum);
            }
            List<CompactingMatch.ConsumptionEntry> consumption = merged.entrySet().stream()
                    .map(e -> new CompactingMatch.ConsumptionEntry(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            return Optional.of(new CompactingMatch(holder, consumption));
        }
        return Optional.empty();
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