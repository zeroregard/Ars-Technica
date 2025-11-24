package com.zeroregard.ars_technica.recipe;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.zeroregard.ars_technica.helpers.FuseRecipeLike;
import com.zeroregard.ars_technica.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FuseMixingRecipe implements Recipe<RecipeInput>, FuseRecipeLike {
    private final NonNullList<Ingredient> ingredients;
    private final NonNullList<SizedFluidIngredient> fluidIngredients;
    private final NonNullList<FluidStack> fluidResults;
    private final ItemStack result;
    private final HeatCondition requiredHeat;

    public FuseMixingRecipe(NonNullList<Ingredient> ingredients,
                            NonNullList<SizedFluidIngredient> fluidIngredients,
                            NonNullList<FluidStack> fluidResults,
                            ItemStack result,
                            HeatCondition requiredHeat) {
        this.ingredients = ingredients;
        this.fluidIngredients = fluidIngredients;
        this.fluidResults = fluidResults;
        this.result = result;
        this.requiredHeat = requiredHeat;
    }

    @Override
    public boolean matches(@NotNull RecipeInput recipeInput, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput recipeInput, @NotNull HolderLookup.Provider access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider access) {
        return result.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.FUSE_MIXING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeRegistry.FUSE_MIXING_TYPE.get();
    }

    @Override
    public HeatCondition getRequiredHeat() {
        return requiredHeat;
    }

    @Override
    public NonNullList<SizedFluidIngredient> getFluidIngredients() {
        return fluidIngredients;
    }

    @Override
    public NonNullList<FluidStack> getFluidResults() {
        return fluidResults;
    }

    public static class Serializer implements RecipeSerializer<FuseMixingRecipe> {
        @Override
        public com.mojang.serialization.MapCodec<FuseMixingRecipe> codec() {
            return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                    SizedFluidIngredient.NESTED_CODEC.listOf().optionalFieldOf("fluid_ingredients", List.of()).forGetter(recipe -> recipe.fluidIngredients),
                    FluidStack.CODEC.listOf().optionalFieldOf("fluid_results", List.of()).forGetter(recipe -> recipe.fluidResults),
                    ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                    HeatCondition.CODEC.optionalFieldOf("heat", HeatCondition.NONE).forGetter(recipe -> recipe.requiredHeat)
            ).apply(instance, (ingredients, fluidIngredients, fluidResults, result, heat) ->
                    fromCodecValues(ingredients, fluidIngredients, fluidResults, result, heat)));
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FuseMixingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static final StreamCodec<RegistryFriendlyByteBuf, FuseMixingRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public FuseMixingRecipe decode(RegistryFriendlyByteBuf buffer) {
                var ingredients = readList(buffer, Ingredient.CONTENTS_STREAM_CODEC);
                var fluidIngredients = readList(buffer, SizedFluidIngredient.STREAM_CODEC);
                var fluidResults = readList(buffer, FluidStack.STREAM_CODEC);
                ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
                HeatCondition heat = HeatCondition.values()[buffer.readVarInt()];
                return new FuseMixingRecipe(ingredients, fluidIngredients, fluidResults, result, heat);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, FuseMixingRecipe recipe) {
                writeList(buffer, recipe.ingredients, Ingredient.CONTENTS_STREAM_CODEC);
                writeList(buffer, recipe.fluidIngredients, SizedFluidIngredient.STREAM_CODEC);
                writeList(buffer, recipe.fluidResults, FluidStack.STREAM_CODEC);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.result.copy());
                buffer.writeVarInt(recipe.requiredHeat.ordinal());
            }

            private <T> void writeList(RegistryFriendlyByteBuf buffer, List<T> values, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
                buffer.writeVarInt(values.size());
                for (T value : values) {
                    codec.encode(buffer, value);
                }
            }

            private <T> NonNullList<T> readList(RegistryFriendlyByteBuf buffer, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
                int size = buffer.readVarInt();
                NonNullList<T> list = NonNullList.create();
                for (int i = 0; i < size; i++) {
                    list.add(codec.decode(buffer));
                }
                return list;
            }
        };

        private static FuseMixingRecipe fromCodecValues(List<Ingredient> ingredients,
                                                        List<SizedFluidIngredient> fluidIngredients,
                                                        List<FluidStack> fluidResults,
                                                        ItemStack result,
                                                        HeatCondition heat) {
            return new FuseMixingRecipe(copyIntoNonNullList(ingredients),
                    copyIntoNonNullList(fluidIngredients),
                    copyIntoNonNullList(fluidResults),
                    result, heat);
        }
    }

    private static <T> NonNullList<T> copyIntoNonNullList(List<T> source) {
        NonNullList<T> list = NonNullList.create();
        list.addAll(source);
        return list;
    }
}
