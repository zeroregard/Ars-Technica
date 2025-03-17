package com.zeroregard.ars_technica.ponder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ItemProviderWrapper<T extends Item> extends ItemProviderEntry<T, T> {
    private final DeferredHolder<T, T> registryObject;

    public ItemProviderWrapper(AbstractRegistrate<?> registrate, DeferredHolder<T, T> registryObject) {
        super(registrate, registryObject);
        this.registryObject = registryObject;
    }

    @Override
    public ResourceLocation getId() {
        return registryObject.getId();
    }

    @Override
    public T asItem() {
        return registryObject.get();
    }

    @Override
    public ItemStack asStack() {
        return new ItemStack(this.asItem());
    }

    @Override
    public ItemStack asStack(int count) {
        return new ItemStack(this.asItem(), count);
    }
}
