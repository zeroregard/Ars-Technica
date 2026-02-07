package com.zeroregard.ars_technica.helpers;

import com.simibubi.create.AllItems;
import com.zeroregard.ars_technica.registry.DataComponentRegistry;
import net.minecraft.world.item.ItemStack;

public final class ArcaneWrenchHelper {

    public static boolean isArcaneWrench(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        return AllItems.WRENCH.isIn(stack) && stack.has(DataComponentRegistry.ARCANE_WRENCH.get());
    }

    /** Creates a new Arcane Wrench (no enchantments or other data). */
    public static ItemStack createArcaneWrench() {
        ItemStack stack = AllItems.WRENCH.asStack();
        stack.set(DataComponentRegistry.ARCANE_WRENCH.get(), Boolean.TRUE);
        return stack;
    }

    /** Ensures the stack is an Arcane Wrench by copying and adding the component. Preserves enchantments and other components. */
    public static ItemStack ensureArcaneWrench(ItemStack stack) {
        if (stack.isEmpty() || !AllItems.WRENCH.isIn(stack))
            return stack;
        ItemStack out = stack.copy();
        out.set(DataComponentRegistry.ARCANE_WRENCH.get(), Boolean.TRUE);
        return out;
    }
}
