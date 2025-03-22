package com.zeroregard.ars_technica.item;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.zeroregard.ars_technica.client.utils.TooltipUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

public class TransmutationFocus extends ArsNouveauCurio implements ISpellModifierItem {

    public TransmutationFocus(Properties properties) {
        super(properties);
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addAugment(AugmentFortune.INSTANCE);
        return builder;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        var world = context.level();
        TooltipUtils.addOnShift(tooltip2, () -> {
            addInformationAfterShift(stack, world, tooltip2, flagIn);
        }, "focus_of_transmutation");
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformationAfterShift(ItemStack stack, Level world, List<Component> list, TooltipFlag flags) {
        var title = Component.translatable("ars_technica.tooltip.transmutation_focus_shift_info_title").withStyle(ChatFormatting.GOLD);
        list.add(title);

        String[] infoKeys = {
                "ars_technica.tooltip.transmutation_focus_shift_info_speed",
                "ars_technica.tooltip.transmutation_focus_shift_info_process",
                "ars_technica.tooltip.transmutation_focus_shift_info_chance",
                "ars_technica.tooltip.transmutation_focus_shift_info_damage"
        };

        for (String key : infoKeys) {
            list.add(Component.literal(" ").append(Component.translatable(key).withStyle(ChatFormatting.GRAY)));
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
    }
}