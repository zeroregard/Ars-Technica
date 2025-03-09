package com.zeroregard.ars_technica.recipe;


import com.hollingsworth.arsnouveau.api.util.PerkUtil;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ApparatusRecipeInput;
import com.hollingsworth.arsnouveau.common.crafting.recipes.Serializers;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zeroregard.ars_technica.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ITextOutput;


public class TechnomancerArmorRecipe extends EnchantingApparatusRecipe implements ITextOutput {

  public int tier = 3; // 0 indexed

  public TechnomancerArmorRecipe(Ingredient reagent, ItemStack result, List<Ingredient> pedestalItems, int cost) {
    super(reagent, result, pedestalItems, cost, true);
  }

  @Override
  public boolean matches(ApparatusRecipeInput input, Level level) {
    ArmorPerkHolder perkHolder = PerkUtil.getPerkHolder(input.catalyst());
    if (!(perkHolder instanceof ArmorPerkHolder armorPerkHolder)) {
      return false;
    }
    return armorPerkHolder.getTier() == 2 && super.matches(input, level);
  }

  @Override
  public @NotNull ItemStack assemble(ApparatusRecipeInput input, HolderLookup.Provider p_346030_) {
    ItemStack result = super.assemble(input, p_346030_);
    if (!input.catalyst().isComponentsPatchEmpty()) {
      result.applyComponents(input.catalyst().getComponentsPatch());
      result.setDamageValue(0);
    }
    return result;
  }

  @Override
  public @NotNull RecipeType<?> getType() {
    return RecipeRegistry.TECHNOMANCER_ARMOR_UP.get();
  }

  @Override
  public @NotNull RecipeSerializer<?> getSerializer() {
    return RecipeRegistry.TECHNOMANCER_ARMOR_UP_SERIALIZER.get();
  }


  /**
   * Returns the component that should be displayed in the output slot.
   */
  @Override
  public Component getOutputComponent() {
    return Component.translatable("ars_nouveau.armor_upgrade.book_desc", tier);
  }

  @Override
  public boolean excludeJei() {
    return true;
  }

  public static class Serializer implements RecipeSerializer<TechnomancerArmorRecipe> {
    //CODEC
    public @NotNull MapCodec<TechnomancerArmorRecipe> codec() {
      return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, TechnomancerArmorRecipe> streamCodec() {
      return STREAM_CODEC;
    }

    public static MapCodec<TechnomancerArmorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("reagent").forGetter(TechnomancerArmorRecipe::reagent),
            ItemStack.CODEC.fieldOf("result").forGetter(TechnomancerArmorRecipe::result),
            Ingredient.CODEC.listOf().fieldOf("pedestalItems").forGetter(TechnomancerArmorRecipe::pedestalItems),
            Codec.INT.fieldOf("sourceCost").forGetter(TechnomancerArmorRecipe::sourceCost)
    ).apply(instance, TechnomancerArmorRecipe::new));

    public static StreamCodec<RegistryFriendlyByteBuf, TechnomancerArmorRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            TechnomancerArmorRecipe::reagent,
            ItemStack.STREAM_CODEC,
            TechnomancerArmorRecipe::result,
            Serializers.INGREDIENT_LIST_STREAM,
            TechnomancerArmorRecipe::pedestalItems,
            ByteBufCodecs.VAR_INT,
            TechnomancerArmorRecipe::sourceCost,
            TechnomancerArmorRecipe::new
    );
  }

}