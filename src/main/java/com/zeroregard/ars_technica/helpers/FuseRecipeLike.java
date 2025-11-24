package com.zeroregard.ars_technica.helpers;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface FuseRecipeLike {
    HeatCondition getRequiredHeat();
    NonNullList<Ingredient> getIngredients();
    NonNullList<SizedFluidIngredient> getFluidIngredients();
    NonNullList<FluidStack> getFluidResults();
    ItemStack getResultItem(HolderLookup.Provider access);
}
