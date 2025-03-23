package com.zeroregard.ars_technica.client;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.zeroregard.ars_technica.ArsTechnica;

import com.zeroregard.ars_technica.block.PreciseRelayRenderer;
import com.zeroregard.ars_technica.block.SourceMotorRenderer;
import com.zeroregard.ars_technica.client.entity.*;
import com.zeroregard.ars_technica.client.item.SpyMonocleCurioRenderer;
import com.zeroregard.ars_technica.client.sound.EntityLoopingSound;
import com.zeroregard.ars_technica.entity.ArcaneWhirlEntity;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import com.zeroregard.ars_technica.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid = ArsTechnica.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientHandler {

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

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpyMonocleCurioRenderer.SPY_MONOCLE_LAYER, () -> SpyMonocleCurioRenderer.createBodyLayer());
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
        event.registerEntityRenderer(EntityRegistry.ARCANE_PRESS_ENTITY.get(), ArcanePressEntityRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ARCANE_FUSION_ENTITY.get(), ArcaneFusionEntityRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ARCANE_WHIRL_ENTITY.get(), ArcaneWhirlEntityRenderer::new);
        event.registerBlockEntityRenderer(EntityRegistry.SOURCE_MOTOR_BLOCK_ENTITY.get(), SourceMotorRenderer::new);
        event.registerBlockEntityRenderer(EntityRegistry.PRECISE_RELAY_TILE.get(), PreciseRelayRenderer::new);
    }

    private static float DEFAULT_PITCH = 0.8f;
    private static float SPEED_PITCH_MULTIPLIER = 4;
    public static void handleWhirlSound(ArcaneWhirlEntity entity, FanProcessingType processor, float speed) {
        SoundEvent event = getLoopingSoundFromType(processor);
        EntityLoopingSound sound = new EntityLoopingSound(entity, event, 0.5f, DEFAULT_PITCH + SPEED_PITCH_MULTIPLIER * speed);
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    private static SoundEvent getLoopingSoundFromType(FanProcessingType processor) {
        if (processor == AllFanProcessingTypes.HAUNTING) {
            return SoundRegistry.WHIRL_HAUNT.get();
        }
        if (processor == AllFanProcessingTypes.SPLASHING) {
            return SoundRegistry.WHIRL_SPLASH.get();
        }
        if (processor == AllFanProcessingTypes.SMOKING || processor == AllFanProcessingTypes.BLASTING) {
            return SoundRegistry.WHIRL_SMELT.get();
        }
        return SoundRegistry.WHIRL_NONE.get();
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {

    }

    //Curio bag stuff
    @SubscribeEvent
    public static void bindContainerRenderers(RegisterMenuScreensEvent event) {

    }


}