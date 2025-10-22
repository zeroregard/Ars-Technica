package com.zeroregard.ars_technica.recipe;

import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.zeroregard.ars_technica.ArsTechnica;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class SandpaperPolishJeiExtension implements IModPlugin {

    public static final mezz.jei.api.recipe.RecipeType<SandPaperPolishingRecipe> SANDPAPER_POLISHING_TYPE = 
        mezz.jei.api.recipe.RecipeType.create("create", "sandpaper_polishing", SandPaperPolishingRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ArsTechnica.MODID, "sandpaper_polish_extension");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SandpaperPolishRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        assert Minecraft.getInstance().level != null;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        List<SandPaperPolishingRecipe> polishingRecipes = new ArrayList<>();
        for (RecipeHolder<?> recipeHolder : manager.getRecipes()) {
            if (recipeHolder.value() instanceof SandPaperPolishingRecipe recipe) {
                polishingRecipes.add(recipe);
            }
        }
        registration.addRecipes(SANDPAPER_POLISHING_TYPE, polishingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Register catalysts for sandpaper polishing
        registration.addRecipeCatalyst(new ItemStack(com.simibubi.create.AllItems.SAND_PAPER.get()), SANDPAPER_POLISHING_TYPE);
    }
}