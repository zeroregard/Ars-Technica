package net.mcreator.ars_technica.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import software.bernie.geckolib.core.object.Color;

public interface Colorable {
    public Color getColor();
    public double getAlpha();
}
