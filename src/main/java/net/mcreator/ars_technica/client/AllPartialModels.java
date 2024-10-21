package net.mcreator.ars_technica.client;

import com.jozufozu.flywheel.core.PartialModel;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;

public class AllPartialModels {
    public static PartialModel ARCANE_SHAFT_HALF;

    public static void init() {
        ARCANE_SHAFT_HALF = new PartialModel(new ResourceLocation(ArsTechnicaMod.MODID, "block/arcane_shaft_half"));
    }

    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(ARCANE_SHAFT_HALF.getLocation());
    }
}