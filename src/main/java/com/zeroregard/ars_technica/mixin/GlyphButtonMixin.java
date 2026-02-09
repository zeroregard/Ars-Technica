package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.zeroregard.ars_technica.client.gui.SpellCompositeContext;
import com.zeroregard.ars_technica.api.ProcessingTooltipHelper;
import com.zeroregard.ars_technica.saucelib.api.compound.ISubsequentEffectProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

/**
 * When the last effect in the spell is a subsequent-effect provider (e.g. Press),
 * highlights valid next glyphs (e.g. Smelt) with the composite frame and adds a tooltip on shift.
 */
@Mixin(value = GlyphButton.class, remap = false)
public class GlyphButtonMixin {

    private static final ResourceLocation COMPOSITE_EFFECT_TEXTURE = prefix("textures/gui/abstract/composite_effect.png");

    @Inject(method = "renderWidget", at = @At(value = "TAIL"), remap = false)
    private void arsTechnica$renderSubsequentEffectHighlight(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        GlyphButton self = (GlyphButton) (Object) this;
        if (!self.visible || self.abstractSpellPart == null) return;

        SpellCompositeContext context = SpellCompositeContext.getInstance();
        if (!context.isSubsequentEffect(self.abstractSpellPart.getRegistryName())) return;

        int x = self.getX();
        int y = self.getY();

        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 100);

        graphics.blit(COMPOSITE_EFFECT_TEXTURE, x - 1, y - 1, 0, 0, 18, 18, 18, 18);

        int ticks = ClientInfo.ticksInGame;
        int animationLength = 20;
        int delayLength = 30;
        int cycleLength = animationLength + delayLength;
        int cyclePosition = ticks % cycleLength;

        if (cyclePosition < animationLength) {
            float shineProgress = cyclePosition / (float) animationLength;
            float diagonalLength = (float) Math.sqrt(18 * 18 + 18 * 18);
            int shineWidth = 6;
            float diagonalPos = shineProgress * (diagonalLength + shineWidth) - shineWidth;

            for (int px = 0; px < 18; px++) {
                for (int py = 0; py < 18; py++) {
                    float lineOffset = diagonalPos;
                    float distanceToLine = Math.abs(py - (18 - px) - lineOffset) / (float) Math.sqrt(2);

                    if (distanceToLine < shineWidth / 2.0f) {
                        float alphaFactor = 1.0f - (distanceToLine / (shineWidth / 2.0f));
                        int alpha = (int) (255 * alphaFactor * 0.5f);
                        int color = (alpha << 24) | 0xFFFFFF;
                        graphics.fill(x - 1 + px, y - 1 + py, x - 1 + px + 1, y - 1 + py + 1, color);
                    }
                }
            }
        }

        pose.popPose();
    }

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    private void arsTechnica$injectSubsequentEffectTooltip(List<Component> tip, CallbackInfo ci) {
        GlyphButton self = (GlyphButton) (Object) this;
        if (self.abstractSpellPart == null) return;

        SpellCompositeContext context = SpellCompositeContext.getInstance();

        if (context.isSubsequentEffect(self.abstractSpellPart.getRegistryName()) && Screen.hasShiftDown()) {
            AbstractSpellPart lastEffect = context.getLastEffect();
            if (lastEffect instanceof ISubsequentEffectProvider provider) {
                Component tooltip = provider.createSubsequentGlyphTooltip(self.abstractSpellPart.getRegistryName());
                if (tooltip != null) {
                    tip.add(tooltip.copy().withStyle(ChatFormatting.AQUA));
                }
            }
        }

        // Only show active recipe when this button is in the spell strip (the row that removes glyphs on click).
        // Grid buttons (unchosen glyphs) must only show possible recipes (with Shift), never the current spell's recipe.
        int spellStripIndex = context.getSpellStripIndex(self);
        boolean showActiveRecipe = spellStripIndex >= 0;
        ProcessingTooltipHelper.addProcessingTooltipLines(
                self.abstractSpellPart,
                tip,
                Screen.hasShiftDown(),
                showActiveRecipe,
                context.getCurrentSpell(),
                showActiveRecipe ? spellStripIndex : -1);
    }
}
