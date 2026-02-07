package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.simibubi.create.AllItems;
import com.zeroregard.ars_technica.helpers.ArcaneWrenchHelper;
import com.zeroregard.ars_technica.registry.DataComponentRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ensures the runic_spanner recipe result is always the Arcane Wrench stack (Create wrench + component)
 * so JEI and in-game crafting show and produce the correct item.
 * Ars Nouveau may not attach result components when deserializing; we fix any apparatus result that
 * is Create's wrench but missing the arcane_wrench component.
 */
@Mixin(value = EnchantingApparatusRecipe.class, remap = false)
public abstract class ArcaneWrenchEnchantingApparatusRecipeMixin {

    @Inject(method = "getResultItem", at = @At("RETURN"), cancellable = true)
    private void arsTechnica$runicSpannerResult(HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result.isEmpty())
            return;
        if (ArcaneWrenchHelper.isArcaneWrench(result))
            return;
        // Only replace when result is Create's wrench but missing our component (runic_spanner recipe). Preserve enchantments/other data.
        if (AllItems.WRENCH.isIn(result) && !result.has(DataComponentRegistry.ARCANE_WRENCH.get())) {
            cir.setReturnValue(ArcaneWrenchHelper.ensureArcaneWrench(result));
        }
    }
}
