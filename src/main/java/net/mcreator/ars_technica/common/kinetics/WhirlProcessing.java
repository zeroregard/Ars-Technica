package net.mcreator.ars_technica.common.kinetics;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.simibubi.create.content.kinetics.fan.processing.*;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class WhirlProcessing extends FanProcessing {

    public static boolean applyProcessing(ItemEntity entity, FanProcessingType type, Level world, SpellResolver whirlOwner) {
        double processingBoost = 0.0;
        if ( hasTransmutationFocus(whirlOwner)) {
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
        if (shouldDoubleOutputs(whirlOwner)) {
            Optional<ProcessingRecipe<?>> recipe = getProcessingRecipeForEntity(entity, type, world);
            // If there's a processing recipe, we need to check if any of them are chanced based
            if (recipe.get() != null) {
                ArsTechnicaMod.LOGGER.info(recipe.get());
                for (ItemStack stack : stacks) {
                    if (isChanceBased(stack, recipe.get())) {
                        ArsTechnicaMod.LOGGER.info("This is chance based, growing stack for " + stack.getItem());
                        stack.grow(stack.getCount());
                    }
                }
            }
        }

        return stacks;
    }

    private static boolean shouldDoubleOutputs(SpellResolver whirlOwner) {
        if(whirlOwner != null && hasTransmutationFocus(whirlOwner)) {
            return true;
        }
        return false;
    }

    private static boolean hasTransmutationFocus(SpellResolver whirlOwner) {
        return whirlOwner.hasFocus(ItemsRegistry.TRANSMUTATION_FOCUS.get().getDefaultInstance());
    }

    private static Optional<ProcessingRecipe<?>> getProcessingRecipeForEntity(ItemEntity entity, FanProcessingType type, Level world) {
        if (type == AllFanProcessingTypes.SPLASHING) {
            return RecipeHelpers.getSplashingRecipeForItemStack(
                    world.getRecipeManager(),
                    entity.getItem(),
                    world
            ).map(recipe -> recipe);
        }
        else if(type == AllFanProcessingTypes.HAUNTING) {
            return RecipeHelpers.getHauntingRecipeForItemStack(
                    world.getRecipeManager(),
                    entity.getItem(),
                    world
            ).map(recipe -> recipe);
        }
        return Optional.empty();
    }



    private static boolean isChanceBased(ItemStack input, ProcessingRecipe<?> recipe) {
        List<ProcessingOutput> rollables = recipe.getRollableResults();

        return rollables.stream()
                .anyMatch(rollable -> input.getItem() == rollable.getStack().getItem() && rollable.getChance() < 1);
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
            processing.putString("Type", FanProcessingTypeRegistry.getIdOrThrow(type).toString());
            int timeModifierForStackSize = ((entity.getItem().getCount() - 1) / 16) + 1;
            int baseProcessingTime = (int) (AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize) + 1;

            // Apply amplification to reduce processing time
            int processingTime = (int) (baseProcessingTime / (1 + processingBoost));
            processing.putInt("Time", processingTime);
        }

        int value = processing.getInt("Time") - 1;
        processing.putInt("Time", value);
        return value;
    }
}
