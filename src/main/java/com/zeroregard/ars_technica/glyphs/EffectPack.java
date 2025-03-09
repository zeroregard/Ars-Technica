package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.helpers.CraftingHelpers;
import com.zeroregard.ars_technica.helpers.ItemHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class EffectPack extends AbstractItemResolveEffect {
  public static EffectPack INSTANCE = new EffectPack(prefix("glyph_pack"), "Pack");

  private EffectPack(ResourceLocation resourceLocation, String description) {
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

    Map<Item, List<ItemEntity>> packableItems = groupedItems.entrySet().stream()
        .filter(entry -> isPackable(entry.getKey(), world, amplifier))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    for (Map.Entry<Item, List<ItemEntity>> entry : packableItems.entrySet()) {
      packItems(entry.getKey(), entry.getValue(), spellStats, world, pos, amplifier);
    }
  }

  private boolean isPackable(Item item, Level world, double amplifier) {
    int packGridSize = getPackGridSize(amplifier);
    return hasCraftingRecipe(item, packGridSize, world);
  }

  private boolean hasCraftingRecipe(Item item, int gridSize, Level world) {
    NonNullList<ItemStack> mutableItems = NonNullList.withSize(gridSize * gridSize, ItemStack.EMPTY);
    ItemStack itemStack = new ItemStack(item, 1);
    for (int i = 0; i < gridSize * gridSize; i++) {
      mutableItems.set(i, itemStack.copy());
    }
    CraftingInput tempContainer = CraftingInput.of(gridSize, gridSize, mutableItems);
    Optional<CraftingRecipe> recipe = world.getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, tempContainer, world)
            .map(RecipeHolder::value);
    ArsTechnica.LOGGER.info(recipe.isPresent());
    return recipe.isPresent();
  }



  private int getPackGridSize(double amplifier) {
    int value = (int) amplifier + 2;
    // Clamp value just to be safe from other mods adding amplification
    value = Math.max(value, 1);
    value = Math.min(value, 3);
    return value;
  }

  private void packItems(Item item, List<ItemEntity> itemEntities, SpellStats spellStats, Level world,
                         BlockPos pos, double amplifier) {
    int gridSize = getPackGridSize(amplifier);
    int packSize = gridSize * gridSize;
    int totalItemCount = itemEntities.stream().mapToInt(entity -> entity.getItem().getCount()).sum();
    int totalPacks = totalItemCount / packSize;

    NonNullList<ItemStack> mutableItems = NonNullList.withSize(gridSize * gridSize, ItemStack.EMPTY);
    CraftingHelpers.setSquareShape(mutableItems, new ItemStack(item, 1), gridSize);
    CraftingInput container = CraftingInput.of(gridSize, gridSize, List.copyOf(mutableItems));

    for (int i = 0; i < totalPacks; i++) {
      ItemStack packedItem = CraftingHelpers.getItem(container, item, world);
      if (!packedItem.isEmpty()) {
        ItemHelpers.createItemEntity(packedItem, world, pos);
      }
    }

    int totalItemsToRemove = totalPacks * packSize;
    ItemHelpers.subtractItemsFromItemEntities(itemEntities, totalItemsToRemove, item, pos, world);
  }

  @Override
  public int getDefaultManaCost() {
    return 10;
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
    return "Condenses identical items, crafting them into 2x2 recipes. May be augmented with Amplify to use 3x3 recipes instead, or Dampen to craft 1x1 recipes.";
  }

  @Override
  public SpellTier defaultTier() {
    return SpellTier.ONE;
  }
}