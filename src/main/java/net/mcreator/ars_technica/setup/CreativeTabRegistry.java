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

import static net.mcreator.ars_technica.setup.ItemsRegistry.ITEMS;

public class CreativeTabRegistry {

        public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
                        Registries.CREATIVE_MODE_TAB,
                        ArsTechnicaMod.MODID);

        public static final RegistryObject<CreativeModeTab> ARS_TECHNICA_TAB = TABS.register("general", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.ars_technica"))
                .icon(() -> ItemsRegistry.TRANSMUTATION_FOCUS.get().getDefaultInstance())
                .displayItems((params, output) -> {
                    for (var entry : ITEMS.getEntries()) {
                        output.accept(entry.get().getDefaultInstance());
                    }
                }).withTabsBefore(com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry.BLOCKS.getKey().location())
                .build());


        public static void register(IEventBus modEventBus) {
            CreativeTabRegistry.TABS.register(modEventBus);
        }
}