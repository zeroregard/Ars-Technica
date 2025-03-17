package com.zeroregard.ars_technica.ponder;


import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class ATPonderIndex {
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ArsTechnica.MODID);

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        var sourceEngineRegistryObject = ItemRegistry.SOURCE_MOTOR;
        HELPER.forComponents(new ItemProviderWrapper(REGISTRATE, sourceEngineRegistryObject))
                .addStoryBoard("source_motor", SourceMotorScenes::usage);
    }
}