package net.mcreator.ars_technica.setup;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.armor.IGoggleHelmet;
import net.mcreator.ars_technica.armor.TechnomancerArmor;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemsRegistry {

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
      ArsTechnicaMod.MODID);

  public static final RegistryObject<Item> TECHNOMANCER_HELMET = ITEMS.register("technomancer_helmet",
      () -> new TechnomancerArmor(ArmorItem.Type.HELMET, ".create_goggles_info"));

  public static final RegistryObject<Item> TECHNOMANCER_CHESTPLATE = ITEMS.register("technomancer_chestplate",
      () -> new TechnomancerArmor(ArmorItem.Type.CHESTPLATE, null));

  public static final RegistryObject<Item> TECHNOMANCER_LEGGINGS = ITEMS.register("technomancer_leggings",
      () -> new TechnomancerArmor(ArmorItem.Type.LEGGINGS, null));

  public static final RegistryObject<Item> TECHNOMANCER_BOOTS = ITEMS.register("technomancer_boots",
      () -> new TechnomancerArmor(ArmorItem.Type.BOOTS, null));

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
    GogglesItem.addIsWearingPredicate(IGoggleHelmet::isGoggleHelmet);
  }

  public static Item.Properties defaultItemProperties() {
    return new Item.Properties();
  }
}