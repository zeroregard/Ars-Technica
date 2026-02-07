package com.zeroregard.ars_technica.helpers;


import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.entity.fusion.fluids.FluidSourceProvider;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
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
     * Result of matching a set of item entities (and optionally nearby fluids) to a Compacting recipe.
     * Consumption lists indicate which entities to shrink and which fluid sources to drain.
     */
    public record CompactingMatch(RecipeHolder<CompactingRecipe> recipe, List<ConsumptionEntry> consumption, List<FluidConsumptionEntry> fluidConsumption) {
        public record ConsumptionEntry(ItemEntity entity, int count) {}
        public record FluidConsumptionEntry(FluidSourceProvider provider, int amountMb) {}
    }

    /**
     * Finds the first Compacting recipe that can be satisfied by the given item entities and optional nearby fluids.
     * When fluids is null or empty, only item-only recipes are considered. When fluids are provided, recipes with fluid ingredients are also matched.
     * Also supports recipes that use ONLY fluids (e.g. 250mb chocolate -> chocolate bar) as a fallback when no item match is found.
     * Entities are considered in order of distance to posVec so closer items are preferred.
     */
    public static Optional<CompactingMatch> findCompactingMatch(List<ItemEntity> entities, List<FluidSourceProvider> fluids, Vec3 posVec, Level world) {
        List<FluidSourceProvider> availableFluids = fluids != null ? new ArrayList<>(fluids) : List.of();
        boolean hasItems = entities != null && !entities.isEmpty();
        ArsTechnica.LOGGER.info("[findCompactingMatch] hasItems={} entityCount={} fluidCount={}", hasItems, entities != null ? entities.size() : -1, availableFluids.size());
        if (!hasItems && availableFluids.isEmpty()) {
            ArsTechnica.LOGGER.info("[findCompactingMatch] early exit: no items and no fluids");
            return Optional.empty();
        }
        List<ItemEntity> sorted = entities != null && !entities.isEmpty()
                ? new ArrayList<>(entities)
                : new ArrayList<>();
        sorted.removeIf(e -> e == null || e.isRemoved() || e.getItem().isEmpty());
        sorted.sort(Comparator.comparingDouble(e -> e.position().distanceToSqr(posVec)));

        Map<ItemEntity, Integer> pool = new HashMap<>();
        for (ItemEntity e : sorted) {
            int count = e.getItem().getCount();
            if (count > 0) {
                pool.put(e, count);
            }
        }

        @SuppressWarnings("unchecked")
        List<RecipeHolder<CompactingRecipe>> recipes = (List<RecipeHolder<CompactingRecipe>>) (List<?>) world.getRecipeManager().getAllRecipesFor(AllRecipeTypes.COMPACTING.getType());
        ArsTechnica.LOGGER.info("[findCompactingMatch] COMPACTING recipe type={} totalRecipes={}", AllRecipeTypes.COMPACTING.getType(), recipes.size());

        // First try recipes that use items (with optional fluids)
        if (!pool.isEmpty()) {
            for (RecipeHolder<CompactingRecipe> holder : recipes) {
                CompactingRecipe recipe = holder.value();
                if (!recipe.getFluidIngredients().isEmpty() && availableFluids.isEmpty()) {
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
            // Match fluid requirements (same as MixingRecipeHelpers)
            List<CompactingMatch.FluidConsumptionEntry> fluidPlan = new ArrayList<>();
            for (var fluidIngredient : recipe.getFluidIngredients()) {
                var fluidCandidate = availableFluids.stream()
                        .filter(fluid -> fluidIngredient.ingredient().test(fluid.getFluidStack()))
                        .filter(fluid -> fluid.getMbAmount() >= fluidIngredient.amount())
                        .findFirst();
                if (fluidCandidate.isEmpty()) {
                    matched = false;
                    break;
                }
                fluidPlan.add(new CompactingMatch.FluidConsumptionEntry(fluidCandidate.get(), fluidIngredient.amount()));
            }
            if (!matched || fluidPlan.size() != recipe.getFluidIngredients().size()) {
                continue;
            }
            // Merge item plan: (entity, 1) + (entity, 1) -> (entity, 2)
            Map<ItemEntity, Integer> merged = new HashMap<>();
            for (CompactingMatch.ConsumptionEntry entry : plan) {
                merged.merge(entry.entity(), entry.count(), Integer::sum);
            }
            List<CompactingMatch.ConsumptionEntry> consumption = merged.entrySet().stream()
                    .map(e -> new CompactingMatch.ConsumptionEntry(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            return Optional.of(new CompactingMatch(holder, consumption, fluidPlan));
            }
        }

        // Fallback: recipes that use ONLY fluids (e.g. 250mb chocolate -> chocolate bar)
        ArsTechnica.LOGGER.info("[findCompactingMatch] fluid-only fallback: availableFluids={}", availableFluids.size());
        if (!availableFluids.isEmpty()) {
            for (RecipeHolder<CompactingRecipe> holder : recipes) {
                CompactingRecipe recipe = holder.value();
                boolean skipFluidEmpty = recipe.getFluidIngredients().isEmpty();
                boolean skipHasItems = !recipe.getIngredients().isEmpty();
                if (skipFluidEmpty || skipHasItems) {
                    if (!skipFluidEmpty && skipHasItems) ArsTechnica.LOGGER.info("[findCompactingMatch] fluid-only skip recipe {}: has item ingredients", holder.id());
                    continue;
                }
                ArsTechnica.LOGGER.info("[findCompactingMatch] trying fluid-only recipe {} fluidIngredientCount={}", holder.id(), recipe.getFluidIngredients().size());
                List<CompactingMatch.FluidConsumptionEntry> fluidPlan = new ArrayList<>();
                for (var fluidIngredient : recipe.getFluidIngredients()) {
                    int req = fluidIngredient.amount();
                    var fluidCandidate = availableFluids.stream()
                            .filter(fluid -> {
                                boolean test = fluidIngredient.ingredient().test(fluid.getFluidStack());
                                if (!test) ArsTechnica.LOGGER.info("[findCompactingMatch]   ingredient.test failed for fluid {} amount {}mb", fluid.getFluidStack().getFluid(), fluid.getMbAmount());
                                return test;
                            })
                            .filter(fluid -> {
                                boolean ok = fluid.getMbAmount() >= req;
                                if (!ok) ArsTechnica.LOGGER.info("[findCompactingMatch]   amount failed: have {}mb need {}mb", fluid.getMbAmount(), req);
                                return ok;
                            })
                            .findFirst();
                    if (fluidCandidate.isEmpty()) {
                        fluidPlan.clear();
                        ArsTechnica.LOGGER.info("[findCompactingMatch]   no fluid candidate for requirement {}mb", req);
                        break;
                    }
                    fluidPlan.add(new CompactingMatch.FluidConsumptionEntry(fluidCandidate.get(), fluidIngredient.amount()));
                }
                if (fluidPlan.size() == recipe.getFluidIngredients().size()) {
                    ArsTechnica.LOGGER.info("[findCompactingMatch] MATCH fluid-only recipe {}", holder.id());
                    return Optional.of(new CompactingMatch(holder, List.of(), fluidPlan));
                }
            }
            ArsTechnica.LOGGER.info("[findCompactingMatch] no fluid-only recipe matched");
        }
        return Optional.empty();
    }

    public static boolean hasPackingRecipe(Item item, int gridSize, Level world) {
        NonNullList<ItemStack> mutableItems = NonNullList.withSize(gridSize * gridSize, ItemStack.EMPTY);
        CraftingHelpers.setSquareShape(mutableItems, new ItemStack(item, 1), gridSize);
        CraftingInput tempContainer = CraftingInput.of(gridSize, gridSize, List.copyOf(mutableItems));
        return world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, tempContainer, world).isPresent();
    }

    /** Returns true if at least one pack (2x2 or 3x3) is possible with the given entities. Used to avoid spawning the press when nothing can be packed. */
    public static boolean canDoAnyPack(List<ItemEntity> entityList, int gridSize, Level world) {
        if (entityList == null || entityList.isEmpty()) return false;
        int packSize = gridSize * gridSize;
        Map<Item, List<ItemEntity>> grouped = entityList.stream()
                .filter(e -> e != null && !e.isRemoved() && !e.getItem().isEmpty())
                .collect(Collectors.groupingBy(e -> e.getItem().getItem()));
        for (var entry : grouped.entrySet()) {
            Item item = entry.getKey();
            List<ItemEntity> entities = entry.getValue();
            if (!hasPackingRecipe(item, gridSize, world)) continue;
            int total = entities.stream().mapToInt(e -> e.getItem().getCount()).sum();
            if (total < packSize) continue;
            NonNullList<ItemStack> mutableItems = NonNullList.withSize(packSize, ItemStack.EMPTY);
            CraftingHelpers.setSquareShape(mutableItems, new ItemStack(item, 1), gridSize);
            CraftingInput container = CraftingInput.of(gridSize, gridSize, List.copyOf(mutableItems));
            ItemStack packedItem = CraftingHelpers.getItem(container, item, world);
            if (packedItem.isEmpty()) continue;
            return true;
        }
        return false;
    }

    /** Performs one pack (one 2x2 or 3x3 output) from the first packable group with enough items. Returns true if a pack was done. */
    public static boolean tryDoOnePack(List<ItemEntity> entityList, BlockPos pos, int gridSize, Level world) {
        if (entityList == null || entityList.isEmpty()) return false;
        int packSize = gridSize * gridSize;
        Map<Item, List<ItemEntity>> grouped = entityList.stream()
                .filter(e -> e != null && !e.isRemoved() && !e.getItem().isEmpty())
                .collect(Collectors.groupingBy(e -> e.getItem().getItem()));
        for (var entry : grouped.entrySet()) {
            Item item = entry.getKey();
            List<ItemEntity> entities = entry.getValue();
            if (!hasPackingRecipe(item, gridSize, world)) continue;
            int total = entities.stream().mapToInt(e -> e.getItem().getCount()).sum();
            if (total < packSize) continue;
            NonNullList<ItemStack> mutableItems = NonNullList.withSize(packSize, ItemStack.EMPTY);
            CraftingHelpers.setSquareShape(mutableItems, new ItemStack(item, 1), gridSize);
            CraftingInput container = CraftingInput.of(gridSize, gridSize, List.copyOf(mutableItems));
            ItemStack packedItem = CraftingHelpers.getItem(container, item, world);
            if (packedItem.isEmpty()) continue;
            ItemHelpers.createItemEntity(packedItem, world, pos);
            ItemHelpers.subtractItemsFromItemEntitiesInPlace(entities, packSize, item);
            return true;
        }
        return false;
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