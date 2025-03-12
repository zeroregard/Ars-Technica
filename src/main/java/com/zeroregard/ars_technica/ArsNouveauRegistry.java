package com.zeroregard.ars_technica;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.zeroregard.ars_technica.glyphs.EffectCarve;
import com.zeroregard.ars_technica.glyphs.EffectPack;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import com.zeroregard.ars_technica.registry.ModRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.zeroregard.ars_technica.registry.RecipeRegistry;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArsNouveauRegistry {

    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    public static void postInit() {
        addPerkSlots();
    }

    public static void registerGlyphs(){
        register(EffectCarve.INSTANCE);
        register(EffectPack.INSTANCE);
    }
    public static void registerSounds(){
        SpellSoundRegistry.registerSpellSound(ModRegistry.EXAMPLE_SPELL_SOUND);
    }
    public static void register(AbstractSpellPart spellPart){
        GlyphRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

    private static void addPerkSlots() {
        List<PerkSlot> perkSlots = Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE);
        List<ItemLike> armors = List.of(ItemRegistry.TECHNOMANCER_HELMET.get(),
                ItemRegistry.TECHNOMANCER_CHESTPLATE.get(),
                ItemRegistry.TECHNOMANCER_LEGGINGS.get(), ItemRegistry.TECHNOMANCER_BOOTS.get());

        for (ItemLike armor : armors) {
            PerkRegistry.registerPerkProvider(armor, List.of(perkSlots, perkSlots, perkSlots, perkSlots));
        }

        ArsNouveauAPI.getInstance().getEnchantingRecipeTypes().add(RecipeRegistry.TECHNOMANCER_ARMOR_UP.get());
    }
}
