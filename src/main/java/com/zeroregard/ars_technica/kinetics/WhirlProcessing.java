package com.zeroregard.ars_technica.kinetics;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import com.zeroregard.ars_technica.helpers.SpellResolverHelpers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class WhirlProcessing extends FanProcessing {

    public static boolean applyProcessing(ItemEntity entity, FanProcessingType type, Level world, SpellResolver whirlOwner) {
        double processingBoost = 0.0;
        if (SpellResolverHelpers.hasTransmutationFocus(whirlOwner)) {
            processingBoost += 1.0;
        }
        if (decrementProcessingTime(entity, type, processingBoost) != 0)
            return false;
        List<ItemStack> stacks = type.process(entity.getItem(), entity.level());
        if (stacks == null || stacks.isEmpty()) {
            entity.discard();
            return false;
        }
        stacks = applyCustomProcessing(stacks, entity, type, world, whirlOwner);
        entity.setItem(stacks.remove(0));
        for (ItemStack additional : stacks) {
            ItemEntity entityIn = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), additional);
            entityIn.setDeltaMovement(entity.getDeltaMovement());
            entity.level().addFreshEntity(entityIn);
        }

        return true;
    }

    private static List<ItemStack> applyCustomProcessing(List<ItemStack> stacks, ItemEntity entity, FanProcessingType type, Level world, SpellResolver whirlOwner) {
        if (SpellResolverHelpers.shouldDoubleOutputs(whirlOwner)) {
            Optional<ProcessingRecipe<?>> recipe = getProcessingRecipeForEntity(entity, type, world);
            // If there's a processing recipe, we need to check if any of them are chanced based
            if (!recipe.isEmpty()) {
                for (ItemStack stack : stacks) {
                    if (RecipeHelpers.isChanceBased(stack, recipe.get())) {
                        stack.grow(stack.getCount());
                    }
                }
            }
        }

        return stacks;
    }

    private static Optional<ProcessingRecipe<?>> getProcessingRecipeForEntity(ItemEntity entity, FanProcessingType type, Level world) {
        if (type == AllFanProcessingTypes.SPLASHING) {
            return RecipeHelpers.getSplashingRecipeForItemStack(
                    entity.getItem(),
                    world
            ).map(recipe -> recipe);
        }
        else if(type == AllFanProcessingTypes.HAUNTING) {
            return RecipeHelpers.getHauntingRecipeForItemStack(
                    entity.getItem(),
                    world
            ).map(recipe -> recipe);
        }
        return Optional.empty();
    }

    private static ResourceLocation getIdOrThrow(FanProcessingType type) {
        ResourceLocation id = CreateBuiltInRegistries.FAN_PROCESSING_TYPE.getKey(type);
        if (id == null) {
            throw new IllegalArgumentException("Could not get id for FanProcessingType " + type);
        }
        return id;
    }


    private static int decrementProcessingTime(ItemEntity entity, FanProcessingType type, double processingBoost) {
        CompoundTag nbt = entity.getPersistentData();

        if (!nbt.contains("CreateData"))
            nbt.put("CreateData", new CompoundTag());
        CompoundTag createData = nbt.getCompound("CreateData");

        if (!createData.contains("Processing"))
            createData.put("Processing", new CompoundTag());
        CompoundTag processing = createData.getCompound("Processing");

        if (!processing.contains("Type") || AllFanProcessingTypes.parseLegacy(processing.getString("Type")) != type) {
            processing.putString("Type", getIdOrThrow(type).toString());
            int timeModifierForStackSize = ((entity.getItem().getCount() - 1) / 16) + 1;
            int baseProcessingTime = (int) (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1;

            int processingTime = (int) (baseProcessingTime / (1 + processingBoost));
            processing.putInt("Time", processingTime);
        }

        int value = processing.getInt("Time") - 1;
        processing.putInt("Time", value);
        return value;
    }
}
