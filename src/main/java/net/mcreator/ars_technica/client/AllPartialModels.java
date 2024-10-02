package net.mcreator.ars_technica.client;

import com.jozufozu.flywheel.core.PartialModel;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.resources.ResourceLocation;

public class AllPartialModels {
    public static final PartialModel
            ARCANE_SHAFT_HALF = block("arcane_shaft_half");

    private static PartialModel block(String path) {
        return new PartialModel(new ResourceLocation(ArsTechnicaMod.MODID, "block/" + path));
    }

    public static void init() {
        // init static fields ?
    }
}
