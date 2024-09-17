package net.mcreator.ars_technica.common.items.ingredients;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CalibratedPrecisionMechanism extends Item {
    public CalibratedPrecisionMechanism(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
