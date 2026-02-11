package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.zeroregard.ars_technica.api.ProcessingTooltipHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Appends possible processing modes to any glyph item tooltip (inventory, JEI, etc.).
 * No spell context here, so we always pass "not in spell" and the helper adds possible modes when applicable.
 */
@Mixin(value = Glyph.class, remap = false)
public abstract class GlyphItemMixin {

    @Shadow(remap = false)
    public AbstractSpellPart spellPart;

    @Inject(method = "appendHoverText", at = @At("TAIL"), remap = false)
    private void arsTechnica$appendProcessingTooltip(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        ProcessingTooltipHelper.addProcessingTooltipLines(spellPart, tooltip, Screen.hasShiftDown(), false, List.of(), -1);
    }
}
