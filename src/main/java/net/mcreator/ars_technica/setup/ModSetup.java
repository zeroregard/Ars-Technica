package net.mcreator.ars_technica.setup;

import net.minecraftforge.eventbus.api.IEventBus;

public class ModSetup {

  public static void registers(IEventBus modEventBus) {
    GlyphsRegistry.registerGlyphs();
    ItemsRegistry.ITEMS.register(modEventBus);
    CreativeTabRegistry.TABS.register(modEventBus);
    RecipeRegistry.register(modEventBus);
  }
}
