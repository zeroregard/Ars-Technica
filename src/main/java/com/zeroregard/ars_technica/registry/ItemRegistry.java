package com.zeroregard.ars_technica.registry;

import com.hollingsworth.arsnouveau.common.items.ExperienceGem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.zeroregard.ars_technica.armor.IGoggleHelmet;
import com.zeroregard.ars_technica.armor.TechnomancerArmor;
import com.zeroregard.ars_technica.item.RunicSpanner;
import com.zeroregard.ars_technica.item.TransmutationFocus;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
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

    public static final DeferredItem<Item> RUNIC_SPANNER = ITEMS.register("runic_spanner", () -> new RunicSpanner(new Item.Properties().stacksTo(64)));
    public static DeferredItem<Item> TRANSMUTATION_FOCUS = ITEMS.register(
            "transmutation_focus", () -> new TransmutationFocus(
                    new Item.Properties().stacksTo(1)
            ).withTooltip(Component.translatable("ars_technica.tooltip.transmutation_focus"))
    );

    public static int GREATER_EXPERIENCE_VALUE = 12;
    public static DeferredItem<ExperienceGem> GIANT_EXPERIENCE_GEM = ITEMS.register("giant_experience_gem", () -> {
        ExperienceGem gem = new ExperienceGem() {
            @Override
            public int getValue() {
                return GREATER_EXPERIENCE_VALUE * 4;
            }
        };
        gem.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem"));
        return gem;
    });

    public static DeferredItem<ExperienceGem> GARGANTUAN_EXPERIENCE_GEM = ITEMS.register("gargantuan_experience_gem", () -> {
        ExperienceGem gem = new ExperienceGem() {
            @Override
            public int getValue() {
                return GREATER_EXPERIENCE_VALUE * 4 * 4;
            }
        };
        gem.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem"));
        return gem;
    });

    public static final DeferredItem<BlockItem> SOURCE_MOTOR =
            ITEMS.register("source_motor", () -> new BlockItem(BlockRegistry.SOURCE_MOTOR.get(), new Item.Properties().stacksTo(64)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        GogglesItem.addIsWearingPredicate(IGoggleHelmet::isGoggleHelmet);
    }
}
