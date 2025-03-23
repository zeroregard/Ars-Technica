package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.zeroregard.ars_technica.item.PressurePerk;
import com.zeroregard.ars_technica.registry.DataComponentRegistry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimatedMagicArmor.class)
public abstract class AnimatedMagicArmorMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"), remap = false)
    private void inventoryTickInject(ItemStack stack, Level world, Entity player, int slotId, boolean pIsSelected, CallbackInfo ci) {
        if (!world.isClientSide) {
            var holder = PerkUtil.getHolderForPerk(PressurePerk.INSTANCE, (LivingEntity)player);
            if(holder == null || holder.getA() != stack) {
                return;
            }
            int pressurePerkCount = PerkUtil.countForPerk(PressurePerk.INSTANCE, (LivingEntity)player);
            if (pressurePerkCount > 0) {
                var components = stack.getComponents();
                float currentAir = components.getOrDefault(DataComponentRegistry.AIR.get(), 0.0f);
                if(!player.isUnderWater()) {

                    float airGain = (2 * pressurePerkCount - 1) * 0.01f;
                    float airCap = 600 + pressurePerkCount * 300;
                    float newAir = Math.min(currentAir + airGain, airCap);
                    DataComponentPatch patch = DataComponentPatch.builder()
                            .set(DataComponentRegistry.AIR.get(), newAir)
                            .build();
                    stack.applyComponents(patch);
                } else {
                    boolean second = world.getGameTime() % 20 == 0;
                    if(second) {
                        float newAir = currentAir - 1;
                        DataComponentPatch patch = DataComponentPatch.builder()
                                .set(DataComponentRegistry.AIR.get(), newAir)
                                .build();
                        stack.applyComponents(patch);
                    }
                }
            }
        }
        if (world.isClientSide) {
            var holder = PerkUtil.getHolderForPerk(PressurePerk.INSTANCE, (LivingEntity)player);
            if(holder == null || holder.getA() != stack) {
                return;
            }
            int pressurePerkCount = PerkUtil.countForPerk(PressurePerk.INSTANCE, (LivingEntity)player);
            if(pressurePerkCount > 0) {
                var components = stack.getComponents();
                float currentAir = components.getOrDefault(DataComponentRegistry.AIR.get(), 0.0f);
                player.getPersistentData().putInt("VisualBacktankAir", Math.round(currentAir));
            }

        }

    }
}