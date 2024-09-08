package net.mcreator.ars_technica.setup;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import net.mcreator.ars_technica.common.glyphs.EffectCarve;
import net.mcreator.ars_technica.common.glyphs.EffectInsert;
import net.mcreator.ars_technica.common.glyphs.EffectPack;
import net.mcreator.ars_technica.common.glyphs.EffectPress;

public class GlyphsRegistry {

  public static void registerGlyphs() {
    register(EffectPack.INSTANCE);
    register(EffectCarve.INSTANCE);
    register(EffectInsert.INSTANCE);
    register(EffectPress.INSTANCE);
  }

  public static void register(AbstractSpellPart spellPart) {
    GlyphRegistry.registerSpell(spellPart);
  }

}
