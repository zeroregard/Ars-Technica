package com.zeroregard.ars_technica.registry;

import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.zeroregard.ars_technica.armor.ATMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.zeroregard.ars_technica.ArsTechnica.MODID;
import static com.zeroregard.ars_technica.registry.EntityRegistry.ENTITIES;

public class ModRegistry {


    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static void registerRegistries(IEventBus bus) {
        ATMaterials.A_MATERIALS.register(bus);
        BLOCKS.register(bus);
        ENTITIES.register(bus);
        ItemRegistry.register(bus);
        RecipeRegistry.register(bus);
        SoundRegistry.SOUNDS.register(bus);
        ParticleRegistry.PARTICLES.register(bus);


        TABS.register(bus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARS_TECHNICA_TAB;

    static {
        ARS_TECHNICA_TAB = TABS.register("general", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.ars_technica"))
                .icon(() -> ItemRegistry.CALIBRATED_PRECISION_MECHANISM.get().getDefaultInstance())
                .displayItems((params, output) -> {
                    for (var entry : ItemRegistry.ITEMS.getEntries()) {
                        output.accept(entry.get().getDefaultInstance());
                    }
                }).withTabsBefore(CreativeTabRegistry.BLOCKS.getId())
                .build());
    }
}
