package com.zeroregard.ars_technica.helpers;

import com.simibubi.create.AllItems;
import com.zeroregard.ars_technica.registry.DataComponentRegistry;
import net.minecraft.world.item.ItemStack;

/**
 * Arcane Wrench is Create's wrench plus our arcane_wrench component.
 * Create's AllItems.WRENCH is a DeferredHolder; it is only bound after Create
 * registers its items. During RegisterEvent (e.g. when other mods register blocks
 * and call ItemStack.getHoverName()), that holder can still be unbound — accessing
 * it throws NullPointerException. We guard all uses so we never touch it before it's ready.
 */
public final class ArcaneWrenchHelper {

    /** Safe check for Create's wrench; returns false if Create has not registered items yet. */
    public static boolean isCreateWrenchStack(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        try {
            return AllItems.WRENCH.isIn(stack);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isArcaneWrench(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        return isCreateWrenchStack(stack) && stack.has(DataComponentRegistry.ARCANE_WRENCH.get());
    }

    /** Creates a new Arcane Wrench (no enchantments or other data). */
    public static ItemStack createArcaneWrench() {
        try {
            ItemStack stack = AllItems.WRENCH.asStack();
            stack.set(DataComponentRegistry.ARCANE_WRENCH.get(), Boolean.TRUE);
            return stack;
        } catch (NullPointerException e) {
            return ItemStack.EMPTY;
        }
    }

    /** Ensures the stack is an Arcane Wrench by copying and adding the component. Preserves enchantments and other components. */
    public static ItemStack ensureArcaneWrench(ItemStack stack) {
        if (stack.isEmpty() || !isCreateWrenchStack(stack))
            return stack;
        ItemStack out = stack.copy();
        out.set(DataComponentRegistry.ARCANE_WRENCH.get(), Boolean.TRUE);
        return out;
    }
}
