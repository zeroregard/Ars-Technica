package com.zeroregard.ars_technica.helpers;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.zeroregard.ars_technica.registry.ItemRegistry;

public class SpellResolverHelpers {
    public static boolean hasTransmutationFocus(SpellResolver resolver) {
        return resolver.hasFocus(ItemRegistry.TRANSMUTATION_FOCUS.get().getDefaultInstance());
    }

    public static boolean shouldDoubleOutputs(SpellResolver resolver) {
        if(resolver != null && hasTransmutationFocus(resolver)) {
            return true;
        }
        return false;
    }
}
