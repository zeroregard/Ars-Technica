package net.mcreator.ars_technica.mixin;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegistryEntry.class)
public interface RegistryEntryAccessorMixin<T extends ItemLike> {
    @Accessor("delegate")
    RegistryObject<T> getDelegate();
}