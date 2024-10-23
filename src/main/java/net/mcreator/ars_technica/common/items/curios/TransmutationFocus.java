package net.mcreator.ars_technica.common.items.curios;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.mcreator.ars_technica.client.TooltipUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        TooltipUtils.addOnShift(tooltip2, () -> {
            addInformationAfterShift(stack, worldIn, tooltip2, flagIn);
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
}