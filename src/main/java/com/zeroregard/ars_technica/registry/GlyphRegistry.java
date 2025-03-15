package com.zeroregard.ars_technica.registry;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.zeroregard.ars_technica.glyphs.*;

import java.util.ArrayList;
import java.util.List;

public class GlyphRegistry {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    public static void registerGlyphs(){
        register(EffectCarve.INSTANCE);
        register(EffectPack.INSTANCE);
        register(EffectPolish.INSTANCE);
        register(EffectObliterate.INSTANCE);
        register(EffectPress.INSTANCE);
        register(EffectSuperheat.INSTANCE);
    }

    public static void register(AbstractSpellPart spellPart){
        com.hollingsworth.arsnouveau.api.registry.GlyphRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

}
