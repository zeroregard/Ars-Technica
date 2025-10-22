package com.zeroregard.ars_technica.recipe;

import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SandpaperPolishRecipeCategory implements IRecipeCategory<SandPaperPolishingRecipe> {

    private final IDrawable background;
    private final IDrawable icon;
    private final IGuiHelper guiHelper;

    public SandpaperPolishRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.createBlankDrawable(120, 60);
        this.icon = guiHelper.createDrawableItemStack(new net.minecraft.world.item.ItemStack(com.simibubi.create.AllItems.SAND_PAPER.get()));
    }

    @Override
    public @NotNull RecipeType<SandPaperPolishingRecipe> getRecipeType() {
        return SandpaperPolishJeiExtension.SANDPAPER_POLISHING_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("create.recipe.sandpaper_polishing");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SandPaperPolishingRecipe recipe, IFocusGroup focuses) {
        // Add input slot
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 20)
                .addIngredients(recipe.getIngredients().get(0));
        
        // Add output slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 20)
                .addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
    }

    @Override
    public void draw(SandPaperPolishingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw the Polish glyph icon in the bottom left corner
        Minecraft minecraft = Minecraft.getInstance();
        
        // Position the icon in the bottom left corner of the recipe display
        int iconX = 5; // Left margin
        int iconY = 5; // Top margin (will be adjusted to bottom)
        
        // Get the recipe display height to position at bottom
        int recipeHeight = 60; // Height of recipe display
        iconY = recipeHeight - 20; // Position near bottom
        
        // Draw the glyph icon
        ResourceLocation polishGlyphTexture = ResourceLocation.fromNamespaceAndPath("ars_technica", "textures/item/glyph_polish.png");
        guiGraphics.blit(polishGlyphTexture, iconX, iconY, 0, 0, 16, 16, 16, 16);
        
        // Draw a small tooltip text
        Component tooltip = Component.translatable("ars_technica.jei.polish_glyph_tooltip");
        guiGraphics.drawString(minecraft.font, tooltip, iconX + 18, iconY + 4, 0xFFFFFF, false);
    }
}