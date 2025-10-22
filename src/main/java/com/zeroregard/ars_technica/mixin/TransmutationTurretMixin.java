package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.zeroregard.ars_technica.Config;
import com.zeroregard.ars_technica.helpers.SpellResolverHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasicSpellTurretTile.class)
public class TransmutationTurretMixin {

    @Inject(method = "getSourceCost", at = @At("RETURN"), cancellable = true)
    private void modifySourceCost(CallbackInfoReturnable<Integer> cir) {
        BasicSpellTurretTile tile = (BasicSpellTurretTile) (Object) this;
        if (tile instanceof com.zeroregard.ars_technica.block.TransmutationTurretTile) {
            int baseCost = cir.getReturnValue();
            double multiplier = Config.Common.TRANSMUTATION_TURRET_SOURCE_COST_MULTIPLIER.get();
            cir.setReturnValue((int) (baseCost * multiplier));
        }
    }

    @Inject(method = "applyModifiers", at = @At("RETURN"), cancellable = true)
    private void applyTransmutationModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart, SpellContext spellContext, CallbackInfoReturnable<SpellStats.Builder> cir) {
        BasicSpellTurretTile tile = (BasicSpellTurretTile) (Object) this;
        if (tile instanceof com.zeroregard.ars_technica.block.TransmutationTurretTile) {
            // Apply transmutation focus modifiers by adding fortune augment
            builder.addAugment(AugmentFortune.INSTANCE);
        }
    }
}