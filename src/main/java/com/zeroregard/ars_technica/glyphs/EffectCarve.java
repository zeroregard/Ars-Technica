package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.zeroregard.ars_technica.helpers.CraftingHelpers;
import com.zeroregard.ars_technica.helpers.ItemHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class EffectCarve extends AbstractItemResolveEffect {
    public static EffectCarve INSTANCE = new EffectCarve(prefix("glyph_carve"), "Carve");


    private EffectCarve(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
    }

    @Override
    public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
                                  @Nullable LivingEntity shooter,
                                  SpellStats spellStats,
                                  SpellContext spellContext, SpellResolver resolver) {

        double amplifier = spellStats.getAmpMultiplier();

        Map<Item, List<ItemEntity>> groupedItems = entityList.stream()
                .collect(Collectors.groupingBy(itemEntity -> itemEntity.getItem().getItem()));

        for (Map.Entry<Item, List<ItemEntity>> entry : groupedItems.entrySet()) {
            List<ItemEntity> itemEntities = entry.getValue();
            ItemStack exampleStack = itemEntities.get(0).getItem();

            Optional<StonecutterRecipe> cuttingRecipe = getStonecuttingRecipe(exampleStack, world, amplifier);
            if (cuttingRecipe.isPresent()) {
                carveItemStonecutting(cuttingRecipe.get(), itemEntities, world, pos);
            } else {
                var sawingRecipe = getSawingRecipe(exampleStack, world, amplifier);
                if(sawingRecipe.isPresent()) {
                    carveItemSawing(sawingRecipe.get(), itemEntities, world, pos);
                }
            }
        }
    }

    private Optional<StonecutterRecipe> getStonecuttingRecipe(ItemStack input, Level world, double amplifier) {
        SingleRecipeInput inputWrapper = new SingleRecipeInput(input);
        String targetType = amplifier < 0 ? "slab" : amplifier > 0 ? "wall" : "stairs";

        return world.getRecipeManager()
                .getAllRecipesFor(RecipeType.STONECUTTING)
                .stream()
                .filter(holder -> holder.value().matches(inputWrapper, world))
                .map(RecipeHolder::value)
                .filter(recipe -> {
                    String resultName = recipe.getResultItem(world.registryAccess()).getItem().getDescriptionId().toLowerCase();
                    return resultName.contains(targetType);
                })
                .findFirst();
    }


    private Optional<Recipe<RecipeInput>> getSawingRecipe(ItemStack input, Level world, double amplifier) {
        ItemStackHandler itemHandler = new ItemStackHandler(1);
        itemHandler.setStackInSlot(0, input);

        RecipeWrapper inputWrapper = new RecipeWrapper(itemHandler);
        String targetType = amplifier < 0 ? "slab" : "stairs"; // no wood walls

        return world.getRecipeManager()
                .getAllRecipesFor(AllRecipeTypes.CUTTING.getType())
                .stream()
                .filter(holder -> {
                    Recipe<?> recipe = holder.value();
                    return recipe instanceof CuttingRecipe && ((CuttingRecipe) recipe).matches(inputWrapper, world);
                })
                .map(RecipeHolder::value)
                .filter(recipe -> {
                    String resultName = recipe.getResultItem(world.registryAccess()).getItem().getDescriptionId().toLowerCase();
                    return resultName.contains(targetType);
                })
                .findFirst();
    }



    private void carveItemStonecutting(StonecutterRecipe cuttingRecipe, List<ItemEntity> itemEntities, Level world, BlockPos pos) {
        ItemStack inputItem = cuttingRecipe.getIngredients().get(0).getItems()[0];
        ItemStack outputItem = cuttingRecipe.assemble(new SingleRecipeInput(inputItem), world.registryAccess());
        processItemEntities(itemEntities, outputItem, world, pos);
    }

    private void carveItemSawing(Recipe<RecipeInput> sawingRecipe, List<ItemEntity> itemEntities, Level world, BlockPos pos) {
        ItemStack inputItem = sawingRecipe.getIngredients().get(0).getItems()[0];
        ItemStack outputItem = sawingRecipe.assemble(new SingleRecipeInput(inputItem), world.registryAccess());
        processItemEntities(itemEntities, outputItem, world, pos);
    }

    private void processItemEntities(List<ItemEntity> itemEntities, ItemStack outputItem, Level world, BlockPos pos) {
        for (ItemEntity entity : itemEntities) {
            int inputCount = entity.getItem().getCount();
            if (!outputItem.isEmpty()) {
                int outputCount = outputItem.getCount();
                int totalOutput = inputCount * outputCount;

                int maxStackSize = outputItem.getMaxStackSize();

                while (totalOutput > 0) {
                    int countToSpawn = Math.min(totalOutput, maxStackSize);
                    ItemStack outputStack = outputItem.copy();
                    outputStack.setCount(countToSpawn);

                    ItemHelpers.createItemEntity(outputStack, world, pos);
                    totalOutput -= countToSpawn;
                }

                entity.discard();
            }
        }
    }


    private void setContainerShape(NonNullList<ItemStack> items, ItemStack itemStack, double amplifier) {
        if (amplifier < 0.0) {
            CraftingHelpers.setSlabShape(items, itemStack);
        } else if (amplifier > 0.0) {
            CraftingHelpers.setWallShape(items, itemStack);
        } else {
            CraftingHelpers.setStairsShape(items, itemStack);
        }
    }


    // Stairs and walls both have a craft size of 6, slabs have a craft size of 3
    private int getCraftSize(double amplifier) {
        return amplifier < 0 ? 3 : 6;
    }

    private void carveItems(Item item, List<ItemEntity> itemEntities, SpellStats spellStats, Level world,
                            BlockPos pos, double amplifier) {
        int craftSize = getCraftSize(amplifier);
        int totalItemCount = itemEntities.stream().mapToInt(entity -> entity.getItem().getCount()).sum();
        int totalCarvings = totalItemCount / craftSize;

        NonNullList<ItemStack> mutableItems = NonNullList.withSize(9, ItemStack.EMPTY);
        setContainerShape(mutableItems, new ItemStack(item), amplifier);
        CraftingInput container = CraftingInput.of(3, 3, List.copyOf(mutableItems));

        for (int i = 0; i < totalCarvings; i++) {
            ItemStack carvedItem = CraftingHelpers.getItem(container, item, world);
            if (carvedItem.isEmpty()) {
                continue;
            }
            ItemHelpers.createItemEntity(carvedItem, world, pos);
        }

        int totalItemsToRemove = totalCarvings * craftSize;
        ItemHelpers.subtractItemsFromItemEntities(itemEntities, totalItemsToRemove, item, pos, world);
    }




    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Changes recipe to walls");
        map.put(AugmentDampen.INSTANCE, "Changes recipe to slabs");
        map.put(AugmentAOE.INSTANCE, "Increases the area in which items get collected for processing");
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 1);
        defaults.put(AugmentDampen.INSTANCE.getRegistryName(), 1);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public String getBookDescription() {
        return "Shapes identical items, crafting them into stairs. Augment for walls, Dampen for slabs";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

}