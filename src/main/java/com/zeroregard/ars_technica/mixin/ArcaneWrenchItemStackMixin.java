package com.zeroregard.ars_technica.mixin;

import com.simibubi.create.AllItems;
import com.zeroregard.ars_technica.helpers.ArcaneWrenchHelper;
import com.zeroregard.ars_technica.registry.DataComponentRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * When loading an item stack that was saved as ars_technica:runic_spanner, the registry alias
 * turns it into create:wrench and saved components (enchantments, etc.) are preserved.
 * We add the arcane_wrench component so the stack is recognized as an Arcane Wrench.
 * Also overrides display name to "Arcane Wrench" when the component is present.
 */
@Mixin(ItemStack.class)
public class ArcaneWrenchItemStackMixin {

    @Inject(method = "parseOptional", at = @At("RETURN"), cancellable = true)
    private static void arsTechnica$addArcaneWrenchToMigratedRunicSpanner(
            HolderLookup.Provider registries,
            CompoundTag tag,
            CallbackInfoReturnable<ItemStack> cir) {
        arsTechnica$addArcaneWrenchIfRunicSpanner(tag, cir);
    }

    /** World/container load often uses fromNbt, not parseOptional. */
    @Inject(method = "fromNbt", at = @At("RETURN"), cancellable = true)
    private static void arsTechnica$addArcaneWrenchFromNbt(
            HolderLookup.Provider registries,
            CompoundTag tag,
            CallbackInfoReturnable<ItemStack> cir) {
        arsTechnica$addArcaneWrenchIfRunicSpanner(tag, cir);
    }

    /** Optional-returning parse used by some container/codec paths (e.g. chest loading). */
    @Inject(method = "parse", at = @At("RETURN"), cancellable = true)
    private static void arsTechnica$addArcaneWrenchFromParse(
            HolderLookup.Provider registries,
            Tag tag,
            CallbackInfoReturnable<Optional<ItemStack>> cir) {
        if (!(tag instanceof CompoundTag compound))
            return;
        Optional<ItemStack> opt = cir.getReturnValue();
        if (opt.isEmpty())
            return;
        if (!compound.contains("id") || !compound.getString("id").equals("ars_technica:runic_spanner"))
            return;
        ItemStack stack = opt.get();
        if (stack.isEmpty() || !AllItems.WRENCH.isIn(stack) || stack.has(DataComponentRegistry.ARCANE_WRENCH.get()))
            return;
        ItemStack migrated = stack.copy();
        migrated.set(DataComponentRegistry.ARCANE_WRENCH.get(), Boolean.TRUE);
        cir.setReturnValue(Optional.of(migrated));
    }

    private static void arsTechnica$addArcaneWrenchIfRunicSpanner(
            CompoundTag tag,
            CallbackInfoReturnable<ItemStack> cir) {
        if (!tag.contains("id"))
            return;
        String id = tag.getString("id");
        if (!id.equals("ars_technica:runic_spanner"))
            return;
        ItemStack stack = cir.getReturnValue();
        if (stack.isEmpty() || !AllItems.WRENCH.isIn(stack))
            return;
        if (stack.has(DataComponentRegistry.ARCANE_WRENCH.get()))
            return;
        ItemStack migrated = stack.copy();
        migrated.set(DataComponentRegistry.ARCANE_WRENCH.get(), Boolean.TRUE);
        cir.setReturnValue(migrated);
    }

    /** Show "Arcane Wrench" instead of "Wrench" when the stack has the arcane wrench component. */
    @Inject(method = "getHoverName", at = @At("HEAD"), cancellable = true)
    private void arsTechnica$arcaneWrenchDisplayName(CallbackInfoReturnable<Component> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (ArcaneWrenchHelper.isArcaneWrench(self))
            cir.setReturnValue(Component.translatable("item.ars_technica.runic_spanner"));
    }
}
