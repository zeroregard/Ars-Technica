package com.zeroregard.ars_technica.recipe;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FuseMixingRecipeCategory implements IRecipeCategory<FuseMixingRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public FuseMixingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(150, 70);
        ItemStack iconStack = new ItemStack(ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get());
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
    }

    @Override
    public @NotNull RecipeType<FuseMixingRecipe> getRecipeType() {
        return JeiFusePlugin.FUSE_MIXING_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.ars_technica.category.fuse_mixing");
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
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, FuseMixingRecipe recipe, @NotNull IFocusGroup focuses) {
        int column = 0;
        int row = 0;
        for (var ingredient : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 5 + column * 18, 5 + row * 18)
                    .addIngredients(ingredient);
            column++;
            if (column >= 4) {
                column = 0;
                row++;
            }
        }

        RegistryAccess access = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.registryAccess() : RegistryAccess.EMPTY;
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 10)
                .addItemStack(recipe.getResultItem(access));
    }

    @Override
    public void draw(FuseMixingRecipe recipe, @NotNull IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        HeatCondition heat = recipe.getRequiredHeat();
        Component heatText = Component.translatable("jei.ars_technica.category.fuse_mixing.heat", Component.translatable(heat.getTranslationKey()));
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, heatText, 5, 58, 0x3A3A3A, false);
    }
}
