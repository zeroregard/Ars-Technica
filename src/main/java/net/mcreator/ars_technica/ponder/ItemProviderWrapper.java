package net.mcreator.ars_technica.ponder;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

public class ItemProviderWrapper<T extends Item> extends ItemProviderEntry<T> {
    private final RegistryObject<T> registryObject;

    public ItemProviderWrapper(AbstractRegistrate<?> registrate, RegistryObject<T> registryObject) {
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