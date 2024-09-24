package net.mcreator.ars_technica.common.helpers;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

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

    private static <C extends Container, T extends Recipe<C>> Optional<T> getRecipeFor(
            RecipeManager recipeManager, RecipeType<T> recipeType, C container, Level world) {
        return recipeManager.getRecipeFor(recipeType, container, world);
    }
}
