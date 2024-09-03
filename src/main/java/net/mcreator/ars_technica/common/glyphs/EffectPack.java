package net.mcreator.ars_technica.common.glyphs;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.crafting.DynamicCraftingContainer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.mcreator.ars_technica.common.helpers.CraftingHelpers;
import net.mcreator.ars_technica.common.helpers.ItemHelpers;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EffectPack extends AbstractItemResolveEffect {
  public static final EffectPack INSTANCE = new EffectPack();

  private EffectPack() {
    super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_pack"), "Pack");
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
    DynamicCraftingContainer tempContainer = new DynamicCraftingContainer(gridSize, gridSize);
    ItemStack itemStack = new ItemStack(item, gridSize * gridSize);
    for (int i = 0; i < gridSize * gridSize; i++) {
      tempContainer.setItem(i, itemStack);
    }
    Optional<CraftingRecipe> recipe = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, tempContainer, world);
    return recipe.isPresent();
  }

  private int getPackGridSize(double amplifier) {
    if (amplifier == -1.0) {
      return 1;
    } else if (amplifier == 0.0) {
      return 2;
    } else {
      return 3;
    }
  }

  private void packItems(Item item, List<ItemEntity> itemEntities, SpellStats spellStats, Level world,
      BlockPos pos, double amplifier) {
    int gridSize = getPackGridSize(amplifier);
    int packSize = gridSize * gridSize;
    int totalItemCount = itemEntities.stream().mapToInt(entity -> entity.getItem().getCount()).sum();
    int totalPacks = totalItemCount / packSize;

    DynamicCraftingContainer container = new DynamicCraftingContainer(gridSize, gridSize);
    CraftingHelpers.setSquareShape(container, new ItemStack(item), gridSize);

    for (int i = 0; i < totalPacks; i++) {
      ItemStack packedItem = CraftingHelpers.getItem(container, item, world);
      if (packedItem.isEmpty()) {
        continue;
      }
      ItemHelpers.createItemEntity(packedItem, world, pos);
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