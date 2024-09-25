package net.mcreator.ars_technica.client.renderer.entity;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.ArcanePressEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

public class ArcanePressModel extends GeoModel<ArcanePressEntity> {

    @Override
    public void setCustomAnimations(ArcanePressEntity entity, long uniqueID, @Nullable AnimationState<ArcanePressEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(ArcanePressEntity whirl) {
        return new ResourceLocation(ArsTechnicaMod.MODID, "geo/arcane_press.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcanePressEntity whirl) {
        String path = "textures/entity/arcane_press.png";
        return new ResourceLocation(ArsTechnicaMod.MODID, path);
    }

    @Override
    public ResourceLocation getAnimationResource(ArcanePressEntity whirl) {
        return new ResourceLocation(ArsTechnicaMod.MODID, "animations/animations_arcane_press.json");
    }
}
