package net.mcreator.ars_technica.common.helpers;

import net.mcreator.ars_technica.common.crafting.DynamicCraftingContainer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CraftingHelpers {
  public static void setStairsShape(CraftingContainer container, ItemStack itemStack) {
    container.setItem(0, itemStack.copy());
    container.setItem(3, itemStack.copy());
    container.setItem(4, itemStack.copy());
    container.setItem(6, itemStack.copy());
    container.setItem(7, itemStack.copy());
    container.setItem(8, itemStack.copy());
  }

  public static void setSlabShape(CraftingContainer container, ItemStack itemStack) {
    container.setItem(0, itemStack.copy());
    container.setItem(1, itemStack.copy());
    container.setItem(2, itemStack.copy());
  }

  public static void setWallShape(CraftingContainer container, ItemStack itemStack) {
    container.setItem(0, itemStack.copy());
    container.setItem(1, itemStack.copy());
    container.setItem(2, itemStack.copy());
    container.setItem(3, itemStack.copy());
    container.setItem(4, itemStack.copy());
    container.setItem(5, itemStack.copy());
  }

  public static void setSquareShape(CraftingContainer container, ItemStack itemStack, int gridSize) {
    int itemCount = gridSize * gridSize;
    for (int i = 0; i < itemCount; i++) {
      container.setItem(i, itemStack);
    }
  }

  public static ItemStack getItem(CraftingContainer container, Item item, Level world) {
    Optional<CraftingRecipe> recipe = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, world);
    return recipe.map(craftingRecipe -> craftingRecipe.assemble(container, world.registryAccess()))
        .orElse(ItemStack.EMPTY);
  }
}
