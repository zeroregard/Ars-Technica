package net.mcreator.ars_technica.mixin;

import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemProviderEntry.class)
public class ItemProviderEntryMixin<T extends ItemLike> {

    @Inject(method = "isIn", at = @At("HEAD"), cancellable = true, remap = false)
    public void isInOverride(ItemStack stack,  CallbackInfoReturnable<Boolean> cir) {
        RegistryObject<T> delegate = ((RegistryEntryAccessorMixin<T>) this).getDelegate();
        if (stack.getItem() == ItemsRegistry.RUNIC_SPANNER.get()
                && delegate.get().asItem() instanceof WrenchItem) {
            cir.setReturnValue(true);
        }
    }
}
