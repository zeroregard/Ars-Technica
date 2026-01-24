package com.zeroregard.ars_technica;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.zeroregard.ars_technica.glyphs.EffectCarve;
import com.zeroregard.ars_technica.glyphs.EffectPack;
import com.zeroregard.ars_technica.glyphs.EffectPolish;
import com.zeroregard.ars_technica.item.PressurePerk;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.alexthw.sauce.registry.ModRegistry;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArsNouveauRegistry {

    public static void init() {
        registerThreads();
    }

    public static void postInit() {
        addPerkSlots();
    }

    private static void addPerkSlots() {
        List<PerkSlot> perkSlots = Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE);
        
        List<ItemLike> technomancerArmors = List.of(ItemRegistry.TECHNOMANCER_HELMET.get(),
                ItemRegistry.TECHNOMANCER_CHESTPLATE.get(),
                ItemRegistry.TECHNOMANCER_LEGGINGS.get(), ItemRegistry.TECHNOMANCER_BOOTS.get());

        for (ItemLike armor : technomancerArmors) {
            PerkRegistry.registerPerkProvider(armor, List.of(perkSlots, perkSlots, perkSlots, perkSlots));
        }

        List<ItemLike> artificerArmors = List.of(ItemRegistry.ARTIFICER_CAP.get(),
                ItemRegistry.ARTIFICER_TUNIC.get(),
                ItemRegistry.ARTIFICER_PANTS.get(), ItemRegistry.ARTIFICER_SHOES.get());

        for (ItemLike armor : artificerArmors) {
            PerkRegistry.registerPerkProvider(armor, List.of(perkSlots, perkSlots, perkSlots, perkSlots));
        }

        List<ItemLike> machinaguardArmors = List.of(ItemRegistry.MACHINAGUARD_HELMET.get(),
                ItemRegistry.MACHINAGUARD_CHESTPLATE.get(),
                ItemRegistry.MACHINAGUARD_LEGGINGS.get(), ItemRegistry.MACHINAGUARD_BOOTS.get());

        for (ItemLike armor : machinaguardArmors) {
            PerkRegistry.registerPerkProvider(armor, List.of(perkSlots, perkSlots, perkSlots, perkSlots));
        }

        ArsNouveauAPI.getInstance().getEnchantingRecipeTypes().add(ModRegistry.ELEMENTAL_ARMOR_UP.get());
    }

    private static void registerThreads() {
        PerkRegistry.registerPerk(PressurePerk.INSTANCE);
    }
}
