package net.mcreator.ars_technica.common.crafting;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.core.NonNullList;

import java.util.List;

public class DynamicCraftingContainer implements CraftingContainer {

  private final NonNullList<ItemStack> items;
  private final int width;
  private final int height;

  public DynamicCraftingContainer(int width, int height) {
    this.width = width;
    this.height = height;
    this.items = NonNullList.withSize(width * height, ItemStack.EMPTY);
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public List<ItemStack> getItems() {
    return this.items;
  }

  @Override
  public ItemStack getItem(int index) {
    return this.items.get(index);
  }

  @Override
  public void setItem(int index, ItemStack stack) {
    this.items.set(index, stack);
  }

  @Override
  public void clearContent() {
    this.items.clear();
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  @Override
  public int getContainerSize() {
    return this.items.size();
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack itemStack : this.items) {
      if (!itemStack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack removeItem(int index, int count) {
    ItemStack stack = this.items.get(index);
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    if (stack.getCount() <= count) {
      this.items.set(index, ItemStack.EMPTY);
      return stack;
    } else {
      ItemStack result = stack.split(count);
      if (stack.isEmpty()) {
        this.items.set(index, ItemStack.EMPTY);
      }
      return result;
    }
  }

  @Override
  public ItemStack removeItemNoUpdate(int index) {
    ItemStack stack = this.items.get(index);
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    } else {
      this.items.set(index, ItemStack.EMPTY);
      return stack;
    }
  }

  @Override
  public void setChanged() {
    /*
     * This method is called when the container's content changes.
     * It can be used to notify the system that the container has been modified.
     * In this simple implementation, we don't need to handle this specifically.
     */
  }

  @Override
  public void fillStackedContents(StackedContents stackedContents) {
    for (ItemStack itemStack : this.items) {
      stackedContents.accountStack(itemStack);
    }
  }
}
