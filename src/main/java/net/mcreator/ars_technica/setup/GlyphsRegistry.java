package net.mcreator.ars_technica.setup;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import net.mcreator.ars_technica.common.glyphs.*;

public class GlyphsRegistry {

  public static void registerGlyphs() {
    register(EffectPack.INSTANCE);
    register(EffectCarve.INSTANCE);
    register(EffectInsert.INSTANCE);
    register(EffectPress.INSTANCE);
    register(EffectPolish.INSTANCE);
    register(EffectWhirl.INSTANCE);
    register(EffectObliterate.INSTANCE);
    register(EffectFuse.INSTANCE);
  }

  public static void register(AbstractSpellPart spellPart) {
    GlyphRegistry.registerSpell(spellPart);
  }

}
