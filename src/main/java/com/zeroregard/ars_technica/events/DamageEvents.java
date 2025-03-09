package com.zeroregard.ars_technica.events;

import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.simibubi.create.AllDamageTypes;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.armor.TechnomancerArmor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ArsTechnica.MODID)
public class DamageEvents {
    @SubscribeEvent
    public static void bypassRes(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player player) {
            int bonusReduction = 0;
            for (ItemStack stack : event.getEntity().getArmorSlots()) {
                Item item = stack.getItem();
                if (item instanceof TechnomancerArmor && isMechanicalDamageSource(event.getSource())) {
                    bonusReduction++;
                }
            }
            if (bonusReduction > 0) {
                int finalBonusReduction = bonusReduction;
                var manaCap = CapabilityRegistry.getMana(player);
                if (finalBonusReduction > 3) manaCap.addMana(event.getAmount() * 5);
                event.getEntity().addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 200, bonusReduction / 2));
                // Wearing the full set means twice the damage reduction
                if(bonusReduction == 4) {
                    bonusReduction *= 2;
                }
                event.setAmount(event.getAmount() * (1 - bonusReduction / 10F));
            }
        }
    }

    private static boolean isMechanicalDamageSource(DamageSource source) {
        return source.is(AllDamageTypes.CRUSH) ||
                source.is(AllDamageTypes.CUCKOO_SURPRISE) ||
                source.is(AllDamageTypes.DRILL) ||
                source.is(AllDamageTypes.ROLLER) ||
                source.is(AllDamageTypes.SAW) ||
                source.is(AllDamageTypes.RUN_OVER);
    }
}
