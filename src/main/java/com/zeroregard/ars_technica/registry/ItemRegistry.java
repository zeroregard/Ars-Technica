package com.zeroregard.ars_technica.registry;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.zeroregard.ars_technica.armor.IGoggleHelmet;
import com.zeroregard.ars_technica.armor.TechnomancerArmor;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.zeroregard.ars_technica.ArsTechnica.MODID;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> CALIBRATED_PRECISION_MECHANISM = ITEMS.registerSimpleItem("calibrated_precision_mechanism", new Item.Properties().stacksTo(64));

    public static final DeferredItem<Item> TECHNOMANCER_HELMET = ITEMS.register("technomancer_helmet", () -> new TechnomancerArmor(ArmorItem.Type.HELMET, ".create_goggles_info"));
    public static final DeferredItem<Item> TECHNOMANCER_CHESTPLATE = ITEMS.register("technomancer_chestplate", () -> new TechnomancerArmor(ArmorItem.Type.CHESTPLATE, null));
    public static final DeferredItem<Item> TECHNOMANCER_LEGGINGS = ITEMS.register("technomancer_leggings", () -> new TechnomancerArmor(ArmorItem.Type.LEGGINGS, null));
    public static final DeferredItem<Item> TECHNOMANCER_BOOTS = ITEMS.register("technomancer_boots", () -> new TechnomancerArmor(ArmorItem.Type.BOOTS, null));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        GogglesItem.addIsWearingPredicate(IGoggleHelmet::isGoggleHelmet);
    }
}
