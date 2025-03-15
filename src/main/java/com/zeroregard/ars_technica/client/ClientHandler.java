package com.zeroregard.ars_technica.client;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.zeroregard.ars_technica.ArsTechnica;

import com.zeroregard.ars_technica.client.entity.ArcaneHammerEntityRenderer;
import com.zeroregard.ars_technica.client.entity.ArcanePolishEntityRenderer;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;


@EventBusSubscriber(modid = ArsTechnica.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientHandler {


    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {

    }

    @SubscribeEvent
    public static void bindRenderers(final EntityRenderersEvent.RegisterRenderers event) {

    }

    @SubscribeEvent
    public static void initItemColors(final RegisterColorHandlersEvent.Item event) {

        event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
                ItemRegistry.TECHNOMANCER_BOOTS.get());

        event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
                ItemRegistry.TECHNOMANCER_CHESTPLATE.get());

        event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
                ItemRegistry.TECHNOMANCER_HELMET.get());

        event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
                ItemRegistry.TECHNOMANCER_LEGGINGS.get());
    }

    public static int colorFromArmor(ItemStack stack) {
        ArmorPerkHolder holder = PerkUtil.getPerkHolder(stack);
        if (!(holder instanceof ArmorPerkHolder armorPerkHolder))
            return FastColor.ABGR32.opaque(DyeColor.PURPLE.getTextColor());
        return FastColor.ABGR32.opaque(DyeColor.byName(armorPerkHolder.getColor(), DyeColor.PURPLE).getTextColor());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.ARCANE_POLISH_ENTITY.get(), ArcanePolishEntityRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ARCANE_HAMMER_ENTITY.get(), ArcaneHammerEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {

    }

    //Curio bag stuff
    @SubscribeEvent
    public static void bindContainerRenderers(RegisterMenuScreensEvent event) {

    }


}