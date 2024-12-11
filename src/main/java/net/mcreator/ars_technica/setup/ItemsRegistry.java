package net.mcreator.ars_technica.setup;

import com.hollingsworth.arsnouveau.common.items.ExperienceGem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.armor.IGoggleHelmet;
import net.mcreator.ars_technica.armor.TechnomancerArmor;
import net.mcreator.ars_technica.common.items.curios.TransmutationFocus;
import net.mcreator.ars_technica.common.items.equipment.RunicSpanner;
import net.mcreator.ars_technica.common.items.equipment.SpyMonocle;
import net.mcreator.ars_technica.common.items.ingredients.CalibratedPrecisionMechanism;
import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemsRegistry {

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
      ArsTechnicaMod.MODID);

  public static final RegistryObject<Item> TECHNOMANCER_HELMET = ITEMS.register("technomancer_helmet", () -> new TechnomancerArmor(ArmorItem.Type.HELMET, ".create_goggles_info"));
  public static final RegistryObject<Item> TECHNOMANCER_CHESTPLATE = ITEMS.register("technomancer_chestplate", () -> new TechnomancerArmor(ArmorItem.Type.CHESTPLATE, null));
  public static final RegistryObject<Item> TECHNOMANCER_LEGGINGS = ITEMS.register("technomancer_leggings", () -> new TechnomancerArmor(ArmorItem.Type.LEGGINGS, null));
  public static final RegistryObject<Item> TECHNOMANCER_BOOTS = ITEMS.register("technomancer_boots", () -> new TechnomancerArmor(ArmorItem.Type.BOOTS, null));

  public static RegistryObject<Item> TRANSMUTATION_FOCUS = ITEMS.register(
          "transmutation_focus", () -> new TransmutationFocus(
                  defaultItemProperties().stacksTo(1)
          ).withTooltip(Component.translatable("ars_technica.tooltip.transmutation_focus"))
  );

  public static RegistryObject<Item> CALIBRATED_PRECISION_MECHANISM = ITEMS.register("calibrated_precision_mechanism", () -> new CalibratedPrecisionMechanism(defaultItemProperties().stacksTo(64)));
  public static RegistryObject<Item> AMETHYST_DUST = ITEMS.register("amethyst_dust", () -> new Item(defaultItemProperties().stacksTo(64)));
  public static RegistryObject<Item> QUARTZ_DUST = ITEMS.register("quartz_dust", () -> new Item(defaultItemProperties().stacksTo(64)));
  public static RegistryObject<Item> RUNIC_SPANNER = ITEMS.register("runic_spanner", () -> new RunicSpanner(defaultItemProperties().stacksTo(1)));
  public static RegistryObject<Item> SPY_MONOCLE = ITEMS.register("spy_monocle",  () -> new SpyMonocle(defaultItemProperties().stacksTo(1)));

  public static int GREATER_EXPERIENCE_VALUE = 12;
  public static RegistryObject<ExperienceGem> GIANT_EXPERIENCE_GEM = ITEMS.register("giant_experience_gem", () -> {
    ExperienceGem gem = new ExperienceGem() {
      @Override
      public int getValue() {
        return GREATER_EXPERIENCE_VALUE * 4;
      }
    };
    gem.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem"));
    return gem;
  });

  public static RegistryObject<ExperienceGem> GARGANTUAN_EXPERIENCE_GEM = ITEMS.register("gargantuan_experience_gem", () -> {
    ExperienceGem gem = new ExperienceGem() {
      @Override
      public int getValue() {
        return GREATER_EXPERIENCE_VALUE * 4 * 4;
      }
    };
    gem.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem"));
    return gem;
  });

  public static RegistryObject<Item> POCKET_FACTORY_DISC = ITEMS.register("pocket_factory_disc", () -> new RecordItem(3, () -> ArsTechnicaModSounds.POCKET_FACTORY_DISC.get(), defaultItemProperties().stacksTo(1), 3072));

  public static final RegistryObject<BlockItem> SOURCE_ENGINE =
          ITEMS.register("source_engine", () -> new BlockItem(BlockRegistry.SOURCE_ENGINE.get(), defaultItemProperties().stacksTo(64)));

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
    GogglesItem.addIsWearingPredicate(IGoggleHelmet::isGoggleHelmet);
  }


  public static Item.Properties defaultItemProperties() {
    return new Item.Properties();
  }
}