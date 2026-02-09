package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.buttons.CraftingButton;
import com.zeroregard.ars_technica.client.gui.SpellCompositeContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

/**
 * Updates spell crafting context when the spell book validates: sets the current spell
 * and registers the spell-strip slots (CraftingButtons) so we can show the active
 * recipe tooltip only there. Grid glyphs (GlyphButton) are never spell-strip slots.
 * Clears context when the screen is removed to avoid holding references to disposed
 * UI and to prevent stale spell data affecting other UIs.
 */
@Mixin(value = GuiSpellBook.class, remap = false)
public class GuiSpellBookMixin {

    @Inject(method = "validate()V", at = @At("TAIL"), remap = false)
    private void arsTechnica$updateSpellContext(CallbackInfo ci) {
        GuiSpellBook self = (GuiSpellBook) (Object) this;
        SpellCompositeContext context = SpellCompositeContext.getInstance();
        context.setCurrentSpell(self.spell);

        // The spell strip is the row of CraftingButtons (click removes glyph). Register each so
        // CraftingButtonMixin can show "[Create Recipe: X]" there; GlyphButtons stay grid-only.
        for (CraftingButton cell : self.craftingCells) {
            context.setSpellStripSlot(cell, cell.slotNum);
        }
    }

    @Inject(method = "removed()V", at = @At("TAIL"), remap = false)
    private void arsTechnica$clearSpellContextOnClose(CallbackInfo ci) {
        SpellCompositeContext.getInstance().setCurrentSpell(Collections.emptyList());
    }
}
