package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.client.gui.buttons.CraftingButton;
import com.zeroregard.ars_technica.api.ProcessingTooltipHelper;
import com.zeroregard.ars_technica.client.gui.SpellCompositeContext;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Adds the active Create recipe line to the spell-strip slot tooltip (e.g. "[Create Recipe: Press]").
 * CraftingButtons are the spell strip; GlyphButtons are the grid and get possible recipes only.
 */
@Mixin(value = CraftingButton.class, remap = false)
public class CraftingButtonMixin {

    @Inject(method = "getTooltip", at = @At("TAIL"), remap = false)
    private void arsTechnica$addActiveRecipeTooltip(List<Component> tip, CallbackInfo ci) {
        CraftingButton self = (CraftingButton) (Object) this;
        if (self.getAbstractSpellPart() == null) return;

        SpellCompositeContext context = SpellCompositeContext.getInstance();
        ProcessingTooltipHelper.addProcessingTooltipLines(
                self.getAbstractSpellPart(),
                tip,
                false,
                true,
                context.getCurrentSpell(),
                self.slotNum);
    }
}
