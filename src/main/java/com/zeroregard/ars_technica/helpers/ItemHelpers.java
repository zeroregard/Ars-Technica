package com.zeroregard.ars_technica.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ItemHelpers {

  public static void createItemEntity(ItemStack item, Level world, BlockPos pos) {
    ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), item);
    world.addFreshEntity(entity);
  }

  public static void subtractItemsFromItemEntities(List<ItemEntity> itemEntities, int totalItemsToRemove, Item item,
      BlockPos pos, Level world) {
    List<ItemEntity> entitiesToUpdate = new ArrayList<>();
    for (ItemEntity entity : itemEntities) {
      ItemStack stack = entity.getItem();
      int countToRemove = Math.min(stack.getCount(), totalItemsToRemove);
      stack.shrink(countToRemove);
      totalItemsToRemove -= countToRemove;

      if (stack.getCount() > 0) {
        entitiesToUpdate.add(entity);
      } else {
        entity.discard();
      }
    }

    // This is just to get around a visual glitch because Minecraft doesn't like
    // when you update the stack size of an item entity
    for (ItemEntity oldEntity : entitiesToUpdate) {
      int count = oldEntity.getItem().getCount();
      ItemStack newStack = new ItemStack(item, count);
      ItemEntity newEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), newStack);
      world.addFreshEntity(newEntity);
      oldEntity.discard();
    }
  }

  /**
   * Subtracts items in place from the given entities (shrink stacks, discard when empty).
   * Does not create new entities for remainders, so the same list can be reused for multiple
   * operations (e.g. ArcanePressEntity pack mode running multiple cycles).
   */
  public static void subtractItemsFromItemEntitiesInPlace(List<ItemEntity> itemEntities, int totalItemsToRemove, Item item) {
    for (ItemEntity entity : itemEntities) {
      if (totalItemsToRemove <= 0) break;
      ItemStack stack = entity.getItem();
      if (stack.getItem() != item) continue;
      int countToRemove = Math.min(stack.getCount(), totalItemsToRemove);
      stack.shrink(countToRemove);
      totalItemsToRemove -= countToRemove;
      if (stack.isEmpty()) {
        entity.discard();
      }
    }
  }
}
