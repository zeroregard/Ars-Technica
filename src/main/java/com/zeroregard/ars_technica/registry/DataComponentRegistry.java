package com.zeroregard.ars_technica.registry;

import com.mojang.serialization.Codec;
import com.zeroregard.ars_technica.ArsTechnica;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponentRegistry {
    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ArsTechnica.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> AIR = DATA.register("air", () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ZOOMED = DATA.register("zoomed", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build());
    /** When present on Create's wrench, treats it as the "Arcane Wrench" and uses its renderer. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ARCANE_WRENCH = DATA.register("runic_wrench", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build());
}
