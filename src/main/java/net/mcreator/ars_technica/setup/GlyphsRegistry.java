package net.mcreator.ars_technica.setup;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import net.mcreator.ars_technica.common.glyphs.*;

import java.util.ArrayList;
import java.util.List;

public class GlyphsRegistry {
  public static final List<AbstractSpellPart> registeredSpells = new ArrayList<>();

  public static void registerGlyphs() {
    register(EffectPack.INSTANCE);
    register(EffectCarve.INSTANCE);
    register(EffectInsert.INSTANCE);
    register(EffectPress.INSTANCE);
    register(EffectPolish.INSTANCE);
    register(EffectWhirl.INSTANCE);
    register(EffectObliterate.INSTANCE);
    register(EffectFuse.INSTANCE);
    register(EffectTelefeast.INSTANCE);
  }

  public static void register(AbstractSpellPart spellPart) {
    registeredSpells.add(spellPart);
    GlyphRegistry.registerSpell(spellPart);
  }

}
