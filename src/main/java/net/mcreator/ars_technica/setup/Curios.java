package net.mcreator.ars_technica.setup;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

public class Curios {

    public static void init(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(Curios::onInterModEnqueue);
        modEventBus.addListener(Curios::onClientSetup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> modEventBus.addListener(CurioRenderers::onLayerRegister));
    }

    private static void onInterModEnqueue(final InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder()
                .build());
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        CurioRenderers.register();
    }
}