package com.zeroregard.ars_technica.helpers;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ConsumptionHelper {

    public static boolean tryUseEdibleItem(LivingEntity consumer, ItemStack itemStack, Level world) {
        if (isDrink((itemStack))) {
            itemStack.finishUsingItem(world, consumer);
            playSound(SoundEvents.GENERIC_DRINK, world, consumer);
            return true;
        }
        else if (isFood(itemStack, consumer)) {
            consumer.eat(world, itemStack);
            playSound(SoundEvents.GENERIC_EAT, world, consumer);
            return true;
        }
        return false;
    }

    public static boolean isDrink(ItemStack stack) {
        return (stack.getItem() instanceof PotionItem || stack.getUseAnimation() == UseAnim.DRINK);
    }

    public static boolean isFood(ItemStack stack,  @Nullable LivingEntity consumer) {
        return stack.getItem().getFoodProperties(stack, consumer) != null;
    }

    public static boolean tryUseConsumableItem(LivingEntity consumer, ItemStack itemStack, Level world, boolean allowedToUse) {
        if(!allowedToUse) {
            return false;
        }
        // While LivingEntities have interaction hands, only 'Player' can be passed for the 'use' function
        if(consumer instanceof Player player) {
            InteractionHand temporaryHand = InteractionHand.MAIN_HAND;
            ItemStack originalItem = player.getItemInHand(temporaryHand);

            try {
                player.setItemInHand(temporaryHand, itemStack);
                if (isDrink(itemStack)) {
                    itemStack.finishUsingItem(world, player);
                    playSound(SoundEvents.GENERIC_DRINK, world, consumer);
                    return true;
                } else {
                    var result = itemStack.use(world, player, temporaryHand).getResult();
                    if (result == InteractionResult.CONSUME) {
                        playSound(SoundEvents.GENERIC_EAT, world, consumer);
                        return true;
                    }
                }

            } finally {
                player.setItemInHand(temporaryHand, originalItem);
            }
            return false;
        }
        return false;
    }

    public static void playSound(SoundEvent event, Level world, LivingEntity consumer) {
        world.playSound(null, consumer.getX(), consumer.getY(), consumer.getZ(), event, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}
