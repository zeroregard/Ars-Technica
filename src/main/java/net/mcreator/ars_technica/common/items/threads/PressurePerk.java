package net.mcreator.ars_technica.common.items.threads;
import com.hollingsworth.arsnouveau.api.perk.Perk;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.resources.ResourceLocation;

public class PressurePerk extends Perk {

    public static PressurePerk INSTANCE = new PressurePerk();

    public PressurePerk() {
        super(new ResourceLocation(ArsTechnicaMod.MODID, "thread_pressure"));
    }

    @Override
    public String getLangDescription() {
        return "Grants the wielder ultra-high-pressure air within a thread to be used for other tools";
    }

    @Override
    public String getLangName() {
        return "Pressure";
    }

}