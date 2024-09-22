package net.mcreator.ars_technica.mixin;

import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.items.threads.PressurePerk;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimatedMagicArmor.class)
public abstract class AnimatedMagicArmorMixin {

    @Inject(method = "onArmorTick", at = @At("HEAD"))
    private void onArmorTickInject(ItemStack stack, Level world, Player player, CallbackInfo ci) {
        if (!world.isClientSide) {
            LivingEntity playerEntity = player;
            int pressurePerkCount = PerkUtil.countForPerk(PressurePerk.INSTANCE, playerEntity);
            if (pressurePerkCount > 0 && !playerEntity.isUnderWater()) {
                CompoundTag tag = stack.getOrCreateTag();
                float currentAir = tag.getFloat("Air");
                float airGain = (2 * pressurePerkCount - 1) * 0.01f;
                float airCap = 600 + pressurePerkCount * 300;
                float newAir = Math.min(currentAir + airGain, airCap);
                tag.putFloat("Air", newAir);
                stack.setTag(tag);
            }
        }
    }
}