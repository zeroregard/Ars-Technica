package com.zeroregard.ars_technica.armor;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public interface ATGogglesItem {

    static boolean isWearingTechnomancerHelmet(LivingEntity entity) {
        ItemStack headSlot = entity.getItemBySlot(EquipmentSlot.HEAD);
        return headSlot.is(ItemRegistry.TECHNOMANCER_HELMET.get());
    }

    static boolean isWearingSpyMonocle(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        return CuriosApi.getCuriosInventory(player).map(handler ->
                !handler.findCurios(stack -> stack.is(ItemRegistry.SPY_MONOCLE.get())).isEmpty()
        ).orElse(false);
    }
}