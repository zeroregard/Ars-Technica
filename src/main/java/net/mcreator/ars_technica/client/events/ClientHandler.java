package net.mcreator.ars_technica.client.events;

import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.ArsTechnicaMod;

import net.mcreator.ars_technica.client.AllPartialModels;
import net.mcreator.ars_technica.client.renderer.entity.ArcaneHammerEntityRenderer;
import net.mcreator.ars_technica.client.renderer.entity.ArcanePolishEntityRenderer;
import net.mcreator.ars_technica.client.renderer.entity.ArcanePressEntityRenderer;
import net.mcreator.ars_technica.client.renderer.tile.EncasedBasicTurretRenderer;
import net.mcreator.ars_technica.common.blocks.SourceEngineRenderer;
import net.mcreator.ars_technica.common.items.equipment.SpyMonocleCurioRenderer;
import net.mcreator.ars_technica.setup.BlockRegistry;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.client.Minecraft;

import net.mcreator.ars_technica.client.sound.EntityLoopingSound;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.minecraft.sounds.SoundEvent;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.mcreator.ars_technica.client.renderer.entity.WhirlEntityRenderer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsTechnicaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {

    @SubscribeEvent
    public static void initItemColors(final RegisterColorHandlersEvent.Item event) {

        event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
                ItemsRegistry.TECHNOMANCER_BOOTS.get());

        event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
                ItemsRegistry.TECHNOMANCER_CHESTPLATE.get());

        event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
                ItemsRegistry.TECHNOMANCER_HELMET.get());

        event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
                ItemsRegistry.TECHNOMANCER_LEGGINGS.get());
    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        AllPartialModels.init();
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpyMonocleCurioRenderer.SPY_MONOCLE_LAYER, () -> SpyMonocleCurioRenderer.createBodyLayer());
    }

    @SubscribeEvent
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        AllPartialModels.registerAdditionalModels(event);
    }

    private static float DEFAULT_PITCH = 0.8f;
    private static float SPEED_PITCH_MULTIPLIER = 4;

    public static void handleWhirlSound(WhirlEntity entity, FanProcessingType processor, float speed) {
        SoundEvent event = getLoopingSoundFromType(processor);
        EntityLoopingSound sound = new EntityLoopingSound(entity, event, 0.5f, DEFAULT_PITCH + SPEED_PITCH_MULTIPLIER * speed);
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    private static SoundEvent getLoopingSoundFromType(FanProcessingType processor) {
        if (processor == AllFanProcessingTypes.HAUNTING) {
            return ArsTechnicaModSounds.WHIRL_HAUNT.get();
        }
        if (processor == AllFanProcessingTypes.SPLASHING) {
            return ArsTechnicaModSounds.WHIRL_SPLASH.get();
        }
        if (processor == AllFanProcessingTypes.SMOKING || processor == AllFanProcessingTypes.BLASTING) {
            return ArsTechnicaModSounds.WHIRL_SMELT.get();
        }
        return ArsTechnicaModSounds.WHIRL_NONE.get();
    }

    public static int colorFromArmor(ItemStack stack) {
        IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
        if (!(holder instanceof ArmorPerkHolder armorPerkHolder))
            return DyeColor.BROWN.getTextColor();
        return DyeColor.byName(armorPerkHolder.getColor(), DyeColor.BROWN).getTextColor();
    }


    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.WHIRL_ENTITY.get(), WhirlEntityRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ARCANE_PRESS_ENTITY.get(), ArcanePressEntityRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ARCANE_POLISH_ENTITY.get(), ArcanePolishEntityRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ARCANE_HAMMER_ENTITY.get(), ArcaneHammerEntityRenderer::new);

        event.registerBlockEntityRenderer(EntityRegistry.SOURCE_ENGINE_BLOCK_ENTITY.get(), SourceEngineRenderer::new);
        event.registerBlockEntityRenderer(EntityRegistry.ENCASED_TURRET_TILE.get(), EncasedBasicTurretRenderer::new);
    }
}