package com.zeroregard.ars_technica.client.entity;

import com.zeroregard.ars_technica.entity.ArcanePressEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;
import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class ArcanePressModel extends GeoModel<ArcanePressEntity> {

    @Override
    public void setCustomAnimations(ArcanePressEntity entity, long uniqueID, @Nullable AnimationState<ArcanePressEntity> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(ArcanePressEntity entity) {
        return prefix("geo/arcane_press.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArcanePressEntity entity) {
        return prefix( "textures/entity/arcane_press.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArcanePressEntity entity) {
        return prefix( "animations/animations_arcane_press.json");
    }
}
