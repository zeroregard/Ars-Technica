package net.mcreator.ars_technica.setup;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class CreativeTabRegistry {

        public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
                        Registries.CREATIVE_MODE_TAB,
                        ArsTechnicaMod.MODID);

        public static final RegistryObject<CreativeModeTab> ARS_TECHNICA_TAB = TABS.register("ars_technica",
                        () -> CreativeModeTab.builder()
                                        .title(Component.translatable("itemGroup.ars_technica"))
                                        .icon(() -> ItemsRegistry.TRANSMUTATION_FOCUS.get().getDefaultInstance())
                                        .displayItems((params, output) -> {
                                                List.of(
                                                                ItemsRegistry.TRANSMUTATION_FOCUS.get(),
                                                                ItemsRegistry.TECHNOMANCER_HELMET.get(),
                                                                ItemsRegistry.TECHNOMANCER_CHESTPLATE.get(),
                                                                ItemsRegistry.TECHNOMANCER_LEGGINGS.get(),
                                                                ItemsRegistry.TECHNOMANCER_BOOTS.get())
                                                                .forEach(item -> output
                                                                                .accept(item.getDefaultInstance()));
                                        })
                                        .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS) // TODO: should come after Ars Elemental
                                        .build());

        public static void register(IEventBus modEventBus) {
            CreativeTabRegistry.TABS.register(modEventBus);
        }
}