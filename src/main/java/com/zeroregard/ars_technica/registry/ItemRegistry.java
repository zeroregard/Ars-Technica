package com.zeroregard.ars_technica.registry;

import com.hollingsworth.arsnouveau.common.items.ExperienceGem;
import com.hollingsworth.arsnouveau.common.items.RendererBlockItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.zeroregard.ars_technica.armor.ATGogglesItem;
import com.zeroregard.ars_technica.armor.HeavyTechnomancerArmor;
import com.zeroregard.ars_technica.armor.LightTechnomancerArmor;
import com.zeroregard.ars_technica.armor.TechnomancerArmor;
import com.zeroregard.ars_technica.item.SpyMonocle;
import com.zeroregard.ars_technica.item.TransmutationFocus;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.zeroregard.ars_technica.ArsTechnica.MODID;
import static com.zeroregard.ars_technica.registry.SoundRegistry.POCKET_FACTORY_KEY;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> CALIBRATED_PRECISION_MECHANISM = ITEMS.registerSimpleItem("calibrated_precision_mechanism", new Item.Properties().stacksTo(64));

    public static final DeferredItem<Item> TECHNOMANCER_HELMET = ITEMS.register("technomancer_helmet", () -> new TechnomancerArmor(ArmorItem.Type.HELMET, ".create_goggles_info"));
    public static final DeferredItem<Item> TECHNOMANCER_CHESTPLATE = ITEMS.register("technomancer_chestplate", () -> new TechnomancerArmor(ArmorItem.Type.CHESTPLATE, null));
    public static final DeferredItem<Item> TECHNOMANCER_LEGGINGS = ITEMS.register("technomancer_leggings", () -> new TechnomancerArmor(ArmorItem.Type.LEGGINGS, null));
    public static final DeferredItem<Item> TECHNOMANCER_BOOTS = ITEMS.register("technomancer_boots", () -> new TechnomancerArmor(ArmorItem.Type.BOOTS, null));

    public static final DeferredItem<Item> ARTIFICER_CAP = ITEMS.register("artificer_cap", () -> new LightTechnomancerArmor(ArmorItem.Type.HELMET, ".create_goggles_info", defaultArmorProperties(ArmorItem.Type.HELMET)));
    public static final DeferredItem<Item> ARTIFICER_TUNIC = ITEMS.register("artificer_tunic", () -> new LightTechnomancerArmor(ArmorItem.Type.CHESTPLATE, null, defaultArmorProperties(ArmorItem.Type.CHESTPLATE)));
    public static final DeferredItem<Item> ARTIFICER_PANTS = ITEMS.register("artificer_pants", () -> new LightTechnomancerArmor(ArmorItem.Type.LEGGINGS, null, defaultArmorProperties(ArmorItem.Type.LEGGINGS)));
    public static final DeferredItem<Item> ARTIFICER_SHOES = ITEMS.register("artificer_shoes", () -> new LightTechnomancerArmor(ArmorItem.Type.BOOTS, null, defaultArmorProperties(ArmorItem.Type.BOOTS)));

    public static final DeferredItem<Item> MACHINAGUARD_HELMET = ITEMS.register("machinaguard_helmet", () -> new HeavyTechnomancerArmor(ArmorItem.Type.HELMET, ".create_goggles_info"));
    public static final DeferredItem<Item> MACHINAGUARD_CHESTPLATE = ITEMS.register("machinaguard_chestplate", () -> new HeavyTechnomancerArmor(ArmorItem.Type.CHESTPLATE, null));
    public static final DeferredItem<Item> MACHINAGUARD_LEGGINGS = ITEMS.register("machinaguard_leggings", () -> new HeavyTechnomancerArmor(ArmorItem.Type.LEGGINGS, null));
    public static final DeferredItem<Item> MACHINAGUARD_BOOTS = ITEMS.register("machinaguard_boots", () -> new HeavyTechnomancerArmor(ArmorItem.Type.BOOTS, null));

    private static Item.Properties defaultArmorProperties(ArmorItem.Type slot) {
        return new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .component(DataComponentRegistry.ARMOR_PERKS, new com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder())
                .durability(slot.getDurability(30));
    }

    public static DeferredItem<Item> TRANSMUTATION_FOCUS = ITEMS.register(
            "transmutation_focus", () -> new TransmutationFocus(
                    new Item.Properties().stacksTo(1)
            ).withTooltip(Component.translatable("ars_technica.tooltip.transmutation_focus"))
    );

    public static final DeferredItem<Item> SPY_MONOCLE = ITEMS.register("spy_monocle", () -> new SpyMonocle(new Item.Properties().stacksTo(1)));

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

    public static DeferredItem<Item> POCKET_FACTORY = ITEMS.register("pocket_factory", () -> new Item(new Item.Properties().jukeboxPlayable(POCKET_FACTORY_KEY).stacksTo(1)));

    public static final DeferredItem<Item> MARK_OF_TECHNOMANCY = ITEMS.registerSimpleItem("mark_of_technomancy", new Item.Properties().stacksTo(64));

    // New items for music disc crafting - conditional blank disc
    public static final DeferredItem<Item> BLANK_DISC = ITEMS.registerSimpleItem("blank_disc", new Item.Properties().stacksTo(1));

    public static void addGeckoBlockItem(String name, DeferredHolder<Block, ? extends Block> block, String model) {
        ITEMS.register(name, () -> new RendererBlockItem(block.get(), new Item.Properties().stacksTo(64)) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return com.zeroregard.ars_technica.client.item.TransmutationTurretItemRenderer.getISTER();
            }
            
            @Override
            public void appendHoverText(net.minecraft.world.item.ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                double multiplier = com.zeroregard.ars_technica.Config.Common.TRANSMUTATION_TURRET_SOURCE_COST_MULTIPLIER.get();
                tooltip.add(net.minecraft.network.chat.Component.translatable("ars_technica.tooltip.transmutation_turret", String.format("%.1f", multiplier)));
            }
        });
    }

    public static void register(IEventBus bus) {
        // Old runic_spanner item was removed; alias resolves to Create's wrench (ArcaneWrenchItemStackMixin adds the component when loading saved stacks).
        ITEMS.addAlias(
                ResourceLocation.fromNamespaceAndPath(MODID, "runic_spanner"),
                ResourceLocation.fromNamespaceAndPath("create", "wrench"));
        ITEMS.register(bus);
        GogglesItem.addIsWearingPredicate(ATGogglesItem::isWearingTechnomancerHelmet);
        GogglesItem.addIsWearingPredicate(ATGogglesItem::isWearingSpyMonocle);
    }
}
