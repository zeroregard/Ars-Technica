package net.mcreator.ars_technica.setup;

import java.util.Arrays;
import java.util.List;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;

import net.minecraft.world.level.ItemLike;

import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;

public class ArsNouveauRegistry {
  public static void postInit() {
    List<PerkSlot> perkSlots = Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE);
    List<ItemLike> armors = List.of(ItemsRegistry.TECHNOMANCER_HELMET.get(),
        ItemsRegistry.TECHNOMANCER_CHESTPLATE.get(),
        ItemsRegistry.TECHNOMANCER_LEGGINGS.get(), ItemsRegistry.TECHNOMANCER_BOOTS.get());

    for (ItemLike armor : armors) {
      PerkRegistry.registerPerkProvider(armor,
          stack -> new ArmorPerkHolder(stack, List.of(perkSlots, perkSlots, perkSlots, perkSlots)));
    }
  }
}
