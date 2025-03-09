package com.zeroregard.ars_technica.helpers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CraftingHelpers {

  public static void setStairsShape(NonNullList<ItemStack> items, ItemStack itemStack) {
    items.set(0, itemStack.copy());
    items.set(3, itemStack.copy());
    items.set(4, itemStack.copy());
    items.set(6, itemStack.copy());
    items.set(7, itemStack.copy());
    items.set(8, itemStack.copy());
  }

  public static void setSlabShape(NonNullList<ItemStack> items, ItemStack itemStack) {
    items.set(0, itemStack.copy());
    items.set(1, itemStack.copy());
    items.set(2, itemStack.copy());
  }

  public static void setWallShape(NonNullList<ItemStack> items, ItemStack itemStack) {
    items.set(0, itemStack.copy());
    items.set(1, itemStack.copy());
    items.set(2, itemStack.copy());
    items.set(3, itemStack.copy());
    items.set(4, itemStack.copy());
    items.set(5, itemStack.copy());
  }

  public static void setSquareShape(NonNullList<ItemStack> items, ItemStack itemStack, int gridSize) {
    int itemCount = gridSize * gridSize;
    for (int i = 0; i < itemCount; i++) {
      items.set(i, itemStack.copy());
    }
  }

  public static ItemStack getItem(CraftingInput container, Item item, Level world) {
    Optional<CraftingRecipe> recipe = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, world)
            .map(recipeHolder -> recipeHolder.value()); // Unwrap RecipeHolder

    return recipe.map(craftingRecipe -> craftingRecipe.assemble(container, world.registryAccess()))
            .orElse(ItemStack.EMPTY);
  }
}
