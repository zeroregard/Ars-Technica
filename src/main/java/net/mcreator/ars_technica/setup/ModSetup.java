package net.mcreator.ars_technica.setup;

import com.hollingsworth.arsnouveau.setup.registry.ModPotions;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {

  public static void registers(IEventBus modEventBus) {
    GlyphsRegistry.registerGlyphs();
    ItemsRegistry.ITEMS.register(modEventBus);
    CreativeTabRegistry.TABS.register(modEventBus);
  }
}
