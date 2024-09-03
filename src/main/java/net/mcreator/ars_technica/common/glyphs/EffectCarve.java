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
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
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

public class EffectCarve extends AbstractItemResolveEffect {
  public static final EffectCarve INSTANCE = new EffectCarve();

  private EffectCarve() {
    super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_carve"), "Carve");
  }

  @Override
  public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
      @Nullable LivingEntity shooter,
      SpellStats spellStats,
      SpellContext spellContext, SpellResolver resolver) {
    double amplifier = spellStats.getAmpMultiplier();

    Map<Item, List<ItemEntity>> groupedItems = entityList.stream()
        .collect(Collectors.groupingBy(itemEntity -> itemEntity.getItem().getItem()));

    Map<Item, List<ItemEntity>> carveableItems = groupedItems.entrySet().stream()
        .filter(entry -> hasCraftingRecipe(entry.getKey(), world, amplifier))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    for (Map.Entry<Item, List<ItemEntity>> entry : carveableItems.entrySet()) {
      carveItems(entry.getKey(), entry.getValue(), spellStats, world, pos, amplifier);
    }
  }

  private boolean hasCraftingRecipe(Item item, Level world, double amplifier) {
    DynamicCraftingContainer container = new DynamicCraftingContainer(3, 3);
    setContainerShape(container, new ItemStack(item), amplifier);
    Optional<CraftingRecipe> recipe = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, world);
    return recipe.isPresent();
  }

  private void setContainerShape(CraftingContainer container, ItemStack itemStack, double amplifier) {
    if (amplifier < 0.0) {
      CraftingHelpers.setSlabShape(container, itemStack);
    } else if (amplifier > 0.0) {
      CraftingHelpers.setWallShape(container, itemStack);
    } else {
      CraftingHelpers.setStairsShape(container, itemStack);
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

    DynamicCraftingContainer container = new DynamicCraftingContainer(3, 3);
    setContainerShape(container, new ItemStack(item), amplifier);

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