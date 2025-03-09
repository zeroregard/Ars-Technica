package com.zeroregard.ars_technica.registry;

import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.zeroregard.ars_technica.armor.ATMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import static com.zeroregard.ars_technica.ArsTechnica.MODID;
import static com.zeroregard.ars_technica.ArsTechnica.prefix;
import static net.minecraft.core.registries.Registries.SOUND_EVENT;

public class ModRegistry {


    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(SOUND_EVENT, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static void registerRegistries(IEventBus bus) {
        ATMaterials.A_MATERIALS.register(bus);
        BLOCKS.register(bus);
        ItemRegistry.register(bus);
        RecipeRegistry.register(bus);

        SOUNDS.register(bus);
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

    //this is an example of how to register a sound. You also need to add the sound to the sound.json file, referencing your ogg files, and a texture for the button under textures/sounds.
    //this example will use one of the existing sounds randomly
    public static DeferredHolder<SoundEvent, SoundEvent> EXAMPLE_FAMILY = SOUNDS.register("example_sound", () -> makeSound("example_sound"));
    public static SpellSound EXAMPLE_SPELL_SOUND = new SpellSound(ModRegistry.EXAMPLE_FAMILY, Component.literal("Example"), prefix("example_random_sound"));

    static SoundEvent makeSound(@NotNull String name) {
        return SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, name));
    }
}
