package com.zeroregard.ars_technica.mixin;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.CrushingCategory;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.zeroregard.ars_technica.helpers.ObliterateHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.layout.LayoutHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CrushingCategory.class, remap = false)
public abstract class CrushingCategoryMixin {
    @Inject(method = "setRecipe", at = @At("HEAD"), cancellable = true)
    private void arsTechnica$replaceSetRecipe(IRecipeLayoutBuilder builder, AbstractCrushingRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        if (!recipe.getIngredients().isEmpty()) {
            builder
                    .addSlot(RecipeIngredientRole.INPUT, 51, 3)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                    .addIngredients(recipe.getIngredients().get(0));
        }

        IDrawable background = ((CrushingCategory)(Object)this).getBackground();
        int xOffset = background.getWidth() / 2;
        int yOffset = 86;

        if (recipe.getRollableResults().isEmpty()) {
            ci.cancel();
            return;
        }

        LayoutHelper layout = LayoutHelper.centeredHorizontal(recipe.getRollableResults().size(), 1, 18, 18, 1);
        for (ProcessingOutput output : recipe.getRollableResults()) {
            int posX = layout.getX();
            int posY = layout.getY();
            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, xOffset + posX + 1, yOffset + posY + 1)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(arsTechnica$obliterateTooltip(output));
            layout.next();
        }

        ci.cancel();
    }

    private IRecipeSlotRichTooltipCallback arsTechnica$obliterateTooltip(ProcessingOutput output) {
        return (view, tooltip) -> {
            float chance = output.getChance();

            if (chance != 1) {
                tooltip.add(CreateLang.translateDirect("recipe.processing.chance", chance < 0.01 ? "<1" : (int) (chance * 100))
                        .withStyle(ChatFormatting.GOLD));
            }

            if (ObliterateHelper.isChanceCapped(chance)) {
                return;
            }

            boolean shiftDown = Screen.hasShiftDown();
            if (!shiftDown) {
                Component keyName = Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage();
                tooltip.add(Component.empty());
                tooltip.add(ObliterateHelper.createHoldPrompt(keyName));
                return;
            }

            tooltip.add(Component.empty());
            tooltip.addAll(ObliterateHelper.createFortuneDetails(chance));
        };
    }
}

