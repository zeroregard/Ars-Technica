package net.mcreator.ars_technica.mixin;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.mcreator.ars_technica.common.items.threads.PressurePerk;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import java.util.List;

@Mixin(BacktankUtil.class)
public abstract class BacktankUtilMixin {

    @Inject(method="getAllWithAir", at = @At("HEAD"), cancellable = true)
    private static void getAllWithAirFromThread(LivingEntity entity, CallbackInfoReturnable<List<ItemStack>> cir) {
        ItemStack armorWithAir = getItemForPerk(PressurePerk.INSTANCE, entity);
        if(armorWithAir != null) {
            cir.setReturnValue(List.of(armorWithAir));
        }
    }

    private static @Nullable ItemStack getItemForPerk(IPerk perk, LivingEntity entity) {
        ItemStack highestHolderItem = null;
        int maxCount = 0;
        for(ItemStack stack : entity.getArmorSlots()){
            IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
            if(holder == null)
                continue;
            for(PerkInstance instance : holder.getPerkInstances()){
                if(instance.getPerk() == perk){
                    maxCount = Math.max(maxCount, instance.getSlot().value);
                    highestHolderItem = stack;
                }
            }
        }
        return highestHolderItem;
    }
}