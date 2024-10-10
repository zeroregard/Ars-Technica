package net.mcreator.ars_technica.common.helpers;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.mcreator.ars_technica.setup.ItemsRegistry;

public class SpellResolverHelpers {
    public static boolean hasTransmutationFocus(SpellResolver resolver) {
        return resolver.hasFocus(ItemsRegistry.TRANSMUTATION_FOCUS.get().getDefaultInstance());
    }

    public static boolean shouldDoubleOutputs(SpellResolver resolver) {
        if(resolver != null && hasTransmutationFocus(resolver)) {
            return true;
        }
        return false;
    }
}
