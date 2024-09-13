package net.mcreator.ars_technica.common.helpers;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.mcreator.ars_technica.setup.ItemsRegistry;

public class SpellResolverHelpers {
    public static boolean hasTransmutationFocus(SpellResolver whirlOwner) {
        return whirlOwner.hasFocus(ItemsRegistry.TRANSMUTATION_FOCUS.get().getDefaultInstance());
    }
}
