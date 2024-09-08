package net.mcreator.ars_technica.armor;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IGoggleHelmet {

    static boolean isGoggleHelmet(LivingEntity entity) {
        ItemStack headSlot = entity.getItemBySlot(EquipmentSlot.HEAD);
        return headSlot.is(ItemsRegistry.TECHNOMANCER_HELMET.get());
    }
}