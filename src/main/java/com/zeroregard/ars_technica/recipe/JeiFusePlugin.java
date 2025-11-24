package com.zeroregard.ars_technica.recipe;

import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.helpers.FuseRecipeFilter;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import com.zeroregard.ars_technica.registry.RecipeRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class JeiFusePlugin implements IModPlugin {
    public static final RecipeType<FuseMixingRecipe> FUSE_MIXING_TYPE = RecipeType.create(ArsTechnica.MODID, "fuse_mixing", FuseMixingRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ArsTechnica.prefix("fuse_only");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new FuseMixingRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        assert Minecraft.getInstance().level != null;
        var manager = Minecraft.getInstance().level.getRecipeManager();
        List<FuseMixingRecipe> recipes = manager.getAllRecipesFor(RecipeRegistry.FUSE_MIXING_TYPE.get()).stream()
                .filter(holder -> !FuseRecipeFilter.isFiltered(holder.id()))
                .map(RecipeHolder::value)
                .toList();
        registry.addRecipes(FUSE_MIXING_TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(getFuseGlyphStack(), FUSE_MIXING_TYPE);
    }

    private ItemStack getFuseGlyphStack() {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse("ars_nouveau:glyph_fuse"))
                .map(ItemStack::new)
                .orElseGet(() -> new ItemStack(ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get()));
    }
}
