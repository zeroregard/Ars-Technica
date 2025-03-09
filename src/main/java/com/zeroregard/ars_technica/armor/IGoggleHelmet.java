package com.zeroregard.ars_technica.armor;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IGoggleHelmet {

    static boolean isGoggleHelmet(LivingEntity entity) {
        ItemStack headSlot = entity.getItemBySlot(EquipmentSlot.HEAD);
        return headSlot.is(ItemRegistry.TECHNOMANCER_HELMET.get());
    }
}