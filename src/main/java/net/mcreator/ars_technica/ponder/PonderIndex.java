package net.mcreator.ars_technica.ponder;


import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.registries.RegistryObject;

public class PonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(ArsTechnicaMod.MODID);
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ArsTechnicaMod.MODID);
    public static void register() {
        RegistryObject<BlockItem> sourceEngineRegistryObject = ItemsRegistry.SOURCE_ENGINE;
        HELPER.forComponents(new ItemProviderWrapper(REGISTRATE, sourceEngineRegistryObject))
                .addStoryBoard("source_motor", SourceEngineScenes::usage);
    }
}