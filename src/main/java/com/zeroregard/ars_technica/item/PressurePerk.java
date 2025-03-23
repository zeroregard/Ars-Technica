package com.zeroregard.ars_technica.item;

import com.hollingsworth.arsnouveau.api.perk.Perk;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class PressurePerk extends Perk {

    public static PressurePerk INSTANCE = new PressurePerk();

    public PressurePerk() {
        super(prefix("thread_pressure"));
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