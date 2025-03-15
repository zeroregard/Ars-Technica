package com.zeroregard.ars_technica.entity.fusion;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import java.util.List;

public interface ArcaneFusionType {
    String getTextureLocation();
    String getId();
    HeatCondition getSuppliedHeat();
    List<ParticleColor> getParticleColors();
}
