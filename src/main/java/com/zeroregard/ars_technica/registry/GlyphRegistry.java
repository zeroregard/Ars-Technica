package com.zeroregard.ars_technica.registry;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.zeroregard.ars_technica.glyphs.EffectCarve;
import com.zeroregard.ars_technica.glyphs.EffectObliterate;
import com.zeroregard.ars_technica.glyphs.EffectPack;
import com.zeroregard.ars_technica.glyphs.EffectPolish;

import java.util.ArrayList;
import java.util.List;

public class GlyphRegistry {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    public static void registerGlyphs(){
        register(EffectCarve.INSTANCE);
        register(EffectPack.INSTANCE);
        register(EffectPolish.INSTANCE);
        register(EffectObliterate.INSTANCE);
    }

    public static void register(AbstractSpellPart spellPart){
        com.hollingsworth.arsnouveau.api.registry.GlyphRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

}
